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
import Project.OpenBook.Dto.studyProgress.*;
import Project.OpenBook.StudyProgress.studyProgress.*;
import Project.OpenBook.Domain.StudyProgress.TopicProgress.Repository.TopicProgressRepository;
import Project.OpenBook.Domain.StudyProgress.TopicProgress.Domain.TopicProgress;
import Project.OpenBook.Handler.Exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

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
    public void addTopicProgressWrongCount(Customer customer, TopicProgressAddDtoList topicProgressAddDtoList) {
        for (TopicProgressAddDto topicProgressAddDto : topicProgressAddDtoList.getProgressAddDtoList()) {
            Topic topic = checkTopic(topicProgressAddDto.getTopicTitle());

            TopicProgress topicProgress = checkTopicProgress(customer, topic);
            topicProgress.updateWrongCount(topicProgressAddDto.getCount());
        }

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
            chapterProgress.updateProgress(content);


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
            chapterProgress.updateProgress(title);
        }
    }


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
}
