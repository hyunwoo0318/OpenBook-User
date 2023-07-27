package Project.OpenBook.Service;

import Project.OpenBook.Constants.ErrorCode;
import Project.OpenBook.Domain.*;
import Project.OpenBook.Dto.ChapterProgressAddDto;
import Project.OpenBook.Dto.TopicProgressAddDto;
import Project.OpenBook.Repository.chapter.ChapterRepository;
import Project.OpenBook.Repository.chapterprogress.ChapterProgressRepository;
import Project.OpenBook.Repository.chapterprogress.ChapterProgressRepositoryCustom;
import Project.OpenBook.Repository.customer.CustomerRepository;
import Project.OpenBook.Repository.topic.TopicRepository;
import Project.OpenBook.Repository.topicprogress.TopicProgressRepository;
import Project.OpenBook.Utils.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static Project.OpenBook.Constants.ErrorCode.CHAPTER_NOT_FOUND;
import static Project.OpenBook.Constants.ErrorCode.TOPIC_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class StudyProgressService {
    private final ChapterProgressRepository chapterProgressRepository;
    private final TopicProgressRepository topicProgressRepository;
    private final CustomerRepository customerRepository;
    private final ChapterRepository chapterRepository;
    private final TopicRepository topicRepository;


    @Transactional
    public void addChapterProgress(ChapterProgressAddDto chapterProgressAddDto) {
        Chapter chapter = checkChapter(chapterProgressAddDto.getNumber());
        Customer customer = checkCustomer(chapterProgressAddDto.getCustomerId());

        ChapterProgress chapterProgress = chapterProgressRepository.queryChapterProgress(customer.getId(), chapter.getNumber());
        if (chapterProgress == null) {
            ChapterProgress newChapterProgress = new ChapterProgress(customer, chapter);
            chapterProgressRepository.save(newChapterProgress);
            newChapterProgress.updateWrongCount(chapterProgressAddDto.getCount());
        }else{
            chapterProgress.updateWrongCount(chapterProgressAddDto.getCount());
        }
    }

    @Transactional

    public void addTopicProgress(TopicProgressAddDto topicProgressAddDto) {
        Topic topic = checkTopic(topicProgressAddDto.getTopicTitle());
        Customer customer = checkCustomer(topicProgressAddDto.getCustomerId());

        TopicProgress topicProgress = topicProgressRepository.queryTopicProgress(topic.getTitle(), customer.getId());
        if (topicProgress == null) {
            TopicProgress newTopicProgress = new TopicProgress(customer, topic);
            topicProgressRepository.save(newTopicProgress);
            newTopicProgress.updateWrongCount(topicProgressAddDto.getCount());
        }else{
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



}
