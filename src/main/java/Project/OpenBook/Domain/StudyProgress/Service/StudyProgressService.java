package Project.OpenBook.Domain.StudyProgress.Service;

import Project.OpenBook.Domain.Chapter.Domain.Chapter;
import Project.OpenBook.Domain.Chapter.Repo.ChapterRepository;
import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.Customer.Repository.CustomerRepository;
import Project.OpenBook.Domain.StudyProgress.ChapterProgress.Repository.ChapterProgressRepository;
import Project.OpenBook.Domain.StudyProgress.ChapterProgress.Domain.ChapterProgress;
import Project.OpenBook.Domain.StudyProgress.ChapterSection.Repository.ChapterSectionRepository;
import Project.OpenBook.Domain.StudyProgress.Dto.*;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import Project.OpenBook.Domain.Topic.Repo.TopicRepository;
import Project.OpenBook.Domain.StudyProgress.ChapterSection.Domain.ChapterSection;
import Project.OpenBook.Constants.ContentConst;
import Project.OpenBook.Constants.ErrorCode;
import Project.OpenBook.Domain.StudyProgress.TopicProgress.Repository.TopicProgressRepository;
import Project.OpenBook.Domain.StudyProgress.TopicProgress.Domain.TopicProgress;
import Project.OpenBook.Handler.Exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static Project.OpenBook.Constants.ErrorCode.*;
import static Project.OpenBook.Constants.ContentConst.*;
import static Project.OpenBook.Constants.StateConst.LOCKED;
import static Project.OpenBook.Constants.StateConst.OPEN;

@Service
@RequiredArgsConstructor
public class StudyProgressService {
    private final ChapterSectionRepository chapterSectionRepository;
    private final ChapterProgressRepository chapterProgressRepository;
    private final TopicProgressRepository topicProgressRepository;
    private final CustomerRepository customerRepository;
    private final ChapterRepository chapterRepository;
    private final TopicRepository topicRepository;

    @Transactional
    public void addChapterProgressWrongCount(Customer customer, ChapterProgressAddDto chapterProgressAddDto) {
        Chapter chapter = checkChapter(chapterProgressAddDto.getNumber());

        ChapterProgress chapterProgress = checkChapterProgress(customer, chapter);
        chapterProgress.updateWrongCount(chapterProgressAddDto.getCount());
    }

    @Transactional
    public void addTopicProgressWrongCount(Customer customer, List<TopicProgressAddDto> topicProgressAddDtoList) {
        for (TopicProgressAddDto topicProgressAddDto : topicProgressAddDtoList) {
            Topic topic = checkTopic(topicProgressAddDto.getTopicTitle());

            TopicProgress topicProgress = checkTopicProgress(customer, topic);
            topicProgress.updateWrongCount(topicProgressAddDto.getCount());
        }

    }

    @Transactional
    public void updateStudyProgress(Customer customer, ProgressDto progressDto) {
        String content = progressDto.getContent();
        String state = progressDto.getState();
        String title = progressDto.getTitle();

        List<String> chapterContentList = ContentConst.getChapterContent();
        String topicContent = TOPIC_STUDY.getName();
        if(chapterContentList.contains(content)){
            //단원 관련 Content -> title => 단원번호
            Integer chapterNum = Integer.valueOf(title);
            Chapter chapter = checkChapter(chapterNum);

            ChapterProgress chapterProgress = checkChapterProgress(customer, chapter);
            ChapterSection chapterSection = checkChapterSection(customer, chapter, state, content);

            chapterSection.updateState(state);
            boolean ret = hasToUpdate(chapterProgress.getProgress(), content);
            if (ret) {
                chapterProgress.updateProgress(content);
            }

            //다음 단원 학습을 open할 경우 이전 단원을 complete로 설정
            if (content.equals(CHAPTER_INFO.getName()) && chapterNum > 1) {
                Integer prevChapterNum = chapterNum - 1;
                Chapter prevChapter = checkChapter(prevChapterNum);
                ChapterProgress prevChapterProgress = checkChapterProgress(customer, prevChapter);
                prevChapterProgress.updateProgress(COMPLETE.getName());
            }

        }else if(topicContent.equals(content)){
            // 주제 관련 Content -> title = 주제 제목
            Topic topic = checkTopic(title);
            Chapter chapter = topic.getChapter();

            TopicProgress topicProgress = checkTopicProgress(customer, topic);
            ChapterProgress chapterProgress = checkChapterProgress(customer, chapter);
            topicProgress.updateState(state);
            boolean ret = hasToUpdate(chapterProgress.getProgress(), title);
            if (ret) {
                chapterProgress.updateProgress(title);
            }
        }
    }

    private boolean hasToUpdate(String prevProgress, String newProgress) {
        List<String> chapterContentList = ContentConst.getChapterContent();
        Map<String, Integer> map = getNameOrderMap();

        /**
         * 새로운 단원의 단원학습을 오픈하는 경우
         */
        if (newProgress.equals(CHAPTER_INFO.getName())) {
            return true;
        }

        /**
         * 새로운 progress와 기존의 progress가 단원학습, 연표학습, 단원 마무리 문제, 완료일 경우
         * -> order 비교
         */
        if (chapterContentList.contains(newProgress) && chapterContentList.contains(prevProgress)) {
            if (map.get(newProgress) > map.get(prevProgress)) {
                return true;
            }else{
                return false;
            }
        }
        /**
         * 새로운 progresss는 단원학습, 연표학습, 단원마무리 문제이지만 기존 progress는 주제학습일 경우
         * newProgress가 단원마무리문제이면 갱신 단원학습, 연표학습이면 갱신X
         */
        if (chapterContentList.contains(newProgress) && !chapterContentList.contains(prevProgress)) {
            if (newProgress.equals(CHAPTER_COMPLETE_QUESTION.getName())) {
                return true;
            }else{
                return false;
            }
        }

        /**
         * 새로운 progress는 주제학습이지만 기존 progress는 단원학습, 연표학습, 단원마무리 문제인 경우
         * 기존 progress가 연표학습이면 갱신 o, 단원 마무리 문제이면 갱신 X
         */
        if(!chapterContentList.contains(newProgress) && chapterContentList.contains(prevProgress))
        {
            if (prevProgress.equals(TIME_FLOW_STUDY.getName())) {
                return true;
            }else{
                return false;
            }
        }



        /**
         * 기존, 새로운 progress가 모두 주제학습인경우
         * 두개의 주제번호를 따져서 newProgress의 주제번호가 높으면 갱신
         */

         Topic newTopic = checkTopic(newProgress);
         Topic prevTopic = checkTopic(prevProgress);
         if (newTopic.getNumber() > prevTopic.getNumber()) {
              return true;
         }else{
              return false;
         }

    }


    @Transactional(readOnly = true)
    public TotalProgressDto queryTotalProgress(Customer customer) {
        Customer findCustomer = checkCustomer(customer.getId());
        List<ChapterSection> chapterSectionList = findCustomer.getChapterSectionList();
        int openCount =(int) chapterSectionList.stream()
                .filter(cs -> cs.getState().equals(OPEN.getName()))
                .count();

        List<TopicProgress> topicProgressList = findCustomer.getTopicProgressList();
        int topicCount = (int)topicProgressList.stream()
                .filter(tp -> tp.getState().equals(OPEN.getName()))
                .count();


        int ret =(openCount + topicCount) * 100 / (chapterSectionList.size() + topicProgressList.size()) ;
        return new TotalProgressDto(ret);
    }



    private Customer checkCustomer(Long customerId) {
        return customerRepository.findById(customerId).orElseThrow(() -> {
            throw new CustomException(ErrorCode.CUSTOMER_NOT_FOUND);
        });
    }

    private Chapter checkChapter(int num) {
        return chapterRepository.findOneByNumber(num).orElseThrow(() -> {
            throw new CustomException(CHAPTER_NOT_FOUND);
        });
    }

    private Topic checkTopic(String topicTitle) {
        return topicRepository.findTopicByTitle(topicTitle).orElseThrow(() -> {
            throw new CustomException(TOPIC_NOT_FOUND);
        });
    }

    private ChapterProgress checkChapterProgress(Customer customer, Chapter chapter) {
        return chapterProgressRepository.queryChapterProgress(customer.getId(), chapter.getNumber()).orElseGet(() -> {
            ChapterProgress chapterProgress = new ChapterProgress(customer, chapter, 0, NOT_STARTED.getName());
            chapterProgressRepository.save(chapterProgress);
            return chapterProgress;
        });
    }

    private TopicProgress checkTopicProgress(Customer customer, Topic topic) {
        return topicProgressRepository.queryTopicProgress(customer.getId(), topic.getTitle()).orElseGet(() -> {
            TopicProgress topicProgress = new TopicProgress(customer, topic, 0, LOCKED.getName());
            topicProgressRepository.save(topicProgress);
            return topicProgress;
        });
    }

    private ChapterSection checkChapterSection(Customer customer, Chapter chapter, String state, String content) {
        return chapterSectionRepository.queryChapterSection(customer.getId(), chapter.getNumber(), content).orElseGet(() -> {
            ChapterSection chapterSection = new ChapterSection(customer, chapter, content, state);
            chapterSectionRepository.save(chapterSection);
            return chapterSection;
        });
    }
}
