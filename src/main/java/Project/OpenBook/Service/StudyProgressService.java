package Project.OpenBook.Service;

import Project.OpenBook.Constants.ContentConst;
import Project.OpenBook.Constants.ErrorCode;
import Project.OpenBook.Constants.StateConst;
import Project.OpenBook.Domain.*;
import Project.OpenBook.Dto.studyProgress.ChapterProgressAddDto;
import Project.OpenBook.Dto.studyProgress.ProgressDto;
import Project.OpenBook.Dto.studyProgress.TopicProgressAddDto;
import Project.OpenBook.Dto.studyProgress.TopicProgressAddDtoList;
import Project.OpenBook.Repository.chapter.ChapterRepository;
import Project.OpenBook.Repository.chapterProgress.ChapterProgressRepository;
import Project.OpenBook.Repository.chaptersection.ChapterSectionRepository;
import Project.OpenBook.Repository.customer.CustomerRepository;
import Project.OpenBook.Repository.topic.TopicRepository;
import Project.OpenBook.Repository.topicprogress.TopicProgressRepository;
import Project.OpenBook.Utils.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static Project.OpenBook.Constants.ErrorCode.*;
import static Project.OpenBook.Constants.ContentConst.*;

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

        chapterProgressRepository.queryChapterProgress(customer.getId(), chapter.getNumber())
                .ifPresentOrElse(
                        c -> c.updateWrongCount(chapterProgressAddDto.getCount()),
                () -> {
                    ChapterProgress newChapterProgress = new ChapterProgress(customer, chapter);
                    chapterProgressRepository.save(newChapterProgress);
                    newChapterProgress.updateWrongCount(chapterProgressAddDto.getCount());
                });
    }

    @Transactional
    public void addTopicProgressWrongCount(Customer customer, TopicProgressAddDtoList topicProgressAddDtoList) {
        for (TopicProgressAddDto topicProgressAddDto : topicProgressAddDtoList.getProgressAddDtoList()) {
            Topic topic = checkTopic(topicProgressAddDto.getTopicTitle());

            topicProgressRepository.queryTopicProgress(customer.getId(), topic.getTitle())
                    .ifPresentOrElse(
                            t -> t.updateWrongCount(topicProgressAddDto.getCount()),
                            () -> {
                                TopicProgress newTopicProgress = new TopicProgress(customer, topic);
                                topicProgressRepository.save(newTopicProgress);
                                newTopicProgress.updateWrongCount(topicProgressAddDto.getCount());
                            }
                    );
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

    private void checkState(String state){
        if(!Arrays.stream(StateConst.values())
                .anyMatch(s -> s.getName().equals(state))){
            throw new CustomException(STATE_NOT_FOUND);
        }
    }

    @Transactional
    public void updateStudyProgress(Customer customer, ProgressDto progressDto) {
        String content = progressDto.getContent();
        String state = progressDto.getState();
        String title = progressDto.getTitle();
        Long customerId = customer.getId();

        List<String> chapterContentList = ContentConst.getChapterContent();
        String topicContent = TOPIC_STUDY.getName();
        if(chapterContentList.contains(content)){
            //단원 관련 Content -> title => 단원번호
            Integer chapterNum = Integer.valueOf(title);
            Chapter chapter = checkChapter(chapterNum);
            ChapterProgress chapterProgress;
            Optional<ChapterProgress> chapterProgressOptional = chapterProgressRepository.queryChapterProgress(customerId, chapterNum);
            if (chapterProgressOptional.isEmpty()) {
                chapterProgress = new ChapterProgress(customer, chapter, 0, NOT_STARTED.getName());
                chapterProgressRepository.save(chapterProgress);
            }else{
                chapterProgress= chapterProgressOptional.get();
            }
            chapterSectionRepository.queryChapterSection(customerId, chapterNum, content).ifPresentOrElse(c -> {
                c.updateState(state);
                chapterProgress.updateProgress(content);
            }, () ->{
                ChapterSection chapterSection = new ChapterSection(customer, chapter, content, state);
                chapterSectionRepository.save(chapterSection);
                chapterProgress.updateProgress(content);
            });

            //다음 단원 학습을 open할 경우 이전 단원을 complete로 설정
            if (content.equals(CHAPTER_INFO.getName()) && chapterNum > 1) {
                Integer prevChapterNum = chapterNum - 1;
                Chapter prevChapter = checkChapter(prevChapterNum);
                chapterProgressRepository.queryChapterProgress(customerId, prevChapterNum).ifPresentOrElse(cp -> {
                    cp.updateProgress(COMPLETE.getName());
                }, () -> {
                    ChapterProgress prevChapterProgress = new ChapterProgress(customer, prevChapter, 0, COMPLETE.getName());
                    chapterProgressRepository.save(prevChapterProgress);
                } );
            }
        }else if(topicContent.equals(content)){
            // 주제 관련 Content -> title = 주제 제목
            Topic topic = checkTopic(title);
            TopicProgress topicProgress;
            Optional<TopicProgress> topicProgressOptional = topicProgressRepository.queryTopicProgress(customerId, title);
            if (topicProgressOptional.isEmpty()) {
                topicProgress = new TopicProgress(customer, topic, 0, NOT_STARTED.getName());
                topicProgressRepository.save(topicProgress);
            }else{
                topicProgress = topicProgressOptional.get();
            }
            topicProgress.updateProgress(content);
        }
    }
}
