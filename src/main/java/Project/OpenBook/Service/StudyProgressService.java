package Project.OpenBook.Service;

import Project.OpenBook.Constants.ErrorCode;
import Project.OpenBook.Domain.*;
import Project.OpenBook.Dto.studyProgress.ChapterProgressAddDto;
import Project.OpenBook.Dto.studyProgress.ProgressDto;
import Project.OpenBook.Dto.studyProgress.TopicProgressAddDto;
import Project.OpenBook.Dto.studyProgress.TopicProgressAddDtoList;
import Project.OpenBook.Repository.chapter.ChapterRepository;
import Project.OpenBook.Repository.chapterprogress.ChapterProgressRepository;
import Project.OpenBook.Repository.customer.CustomerRepository;
import Project.OpenBook.Repository.topic.TopicRepository;
import Project.OpenBook.Repository.topicprogress.TopicProgressRepository;
import Project.OpenBook.Utils.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.util.Arrays;
import java.util.List;

import static Project.OpenBook.Constants.ErrorCode.*;
import static Project.OpenBook.Constants.ProgressConst.*;

@Service
@RequiredArgsConstructor
public class StudyProgressService {
    private final ChapterProgressRepository chapterProgressRepository;
    private final TopicProgressRepository topicProgressRepository;
    private final CustomerRepository customerRepository;
    private final ChapterRepository chapterRepository;
    private final TopicRepository topicRepository;

    private final List<String> progressList = Arrays.asList(NOT_STARTED, CHAPTER_INFO, TIME_FLOW_STUDY, TIME_FLOW_QUESTION, GET_TOPIC_BY_KEYWORD, GET_TOPIC_BY_SENTENCE, COMPLETE);


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

            topicProgressRepository.queryTopicProgress(topic.getTitle(), customer.getId())
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

    public boolean checkProgress(String progress){
        return progressList.contains(progress);
    }

    @Transactional
    public void updateChapterProgress(Customer customer, ProgressDto progressDto) {
        String progress = progressDto.getProgress();
        if(!checkProgress(progress)){
            throw new CustomException(PROGRESS_NOT_FOUND);
        }
        Integer chapterNum = progressDto.getNumber();
        Chapter chapter = checkChapter(chapterNum);
        chapterProgressRepository.queryChapterProgress(customer.getId(), chapterNum).ifPresentOrElse(cp ->{
                     cp.updateProgress(progress);
                     },
                () -> {
                    ChapterProgress chapterProgress = new ChapterProgress(customer, chapter, progress);
                    chapterProgressRepository.save(chapterProgress);
        });
    }
}
