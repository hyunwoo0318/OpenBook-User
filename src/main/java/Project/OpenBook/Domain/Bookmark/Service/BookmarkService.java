package Project.OpenBook.Domain.Bookmark.Service;

import Project.OpenBook.Domain.Bookmark.Domain.Bookmark;
import Project.OpenBook.Domain.Bookmark.Dto.BookmarkDto;
import Project.OpenBook.Domain.Bookmark.Repository.BookmarkRepository;
import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.LearningRecord.TimelineLearningRecord.Domain.TimelineLearningRecord;
import Project.OpenBook.Domain.LearningRecord.TimelineLearningRecord.Repo.TimelineLearningRecordRepository;
import Project.OpenBook.Domain.LearningRecord.TopicLearningRecord.Domain.TopicLearningRecord;
import Project.OpenBook.Domain.LearningRecord.TopicLearningRecord.Repo.TopicLearningRecordRepository;
import Project.OpenBook.Domain.Timeline.Domain.Timeline;
import Project.OpenBook.Domain.Timeline.Repo.TimelineRepository;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import Project.OpenBook.Domain.Topic.Repo.TopicRepository;
import Project.OpenBook.Handler.Exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static Project.OpenBook.Constants.ErrorCode.TIMELINE_NOT_FOUND;
import static Project.OpenBook.Constants.ErrorCode.TOPIC_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class BookmarkService {
    private final BookmarkRepository bookmarkRepository;
    private final TopicRepository topicRepository;
    private final TimelineRepository timelineRepository;

    private final TopicLearningRecordRepository topicLearningRecordRepository;
    private final TimelineLearningRecordRepository timelineLearningRecordRepository;


    @Transactional
    public void addBookmark(Customer customer, BookmarkDto dto) {

        String topicTitle = dto.getTopicTitle();
        Long timelineId = dto.getId();

        if (topicTitle != null) {
            TopicLearningRecord record = getTopicLearningRecord(customer, topicTitle);
            record.updateBookmark(true);
        }else{
            TimelineLearningRecord record = getTimelineLearningRecord(customer, timelineId);
            record.updateBookmark(true);
        }
    }



    @Transactional
    public void deleteBookmark(Customer customer, BookmarkDto dto) {
        String topicTitle = dto.getTopicTitle();
        Long timelineId = dto.getId();

        if (topicTitle != null) {
            TopicLearningRecord record = getTopicLearningRecord(customer, topicTitle);
            record.updateBookmark(false);
        }else{
            TimelineLearningRecord record = getTimelineLearningRecord(customer, timelineId);
            record.updateBookmark(false);
        }
    }

    private TimelineLearningRecord getTimelineLearningRecord(Customer customer, Long timelineId) {
        Timeline timeline = timelineRepository.findById(timelineId).orElseThrow(() -> {
            throw new CustomException(TIMELINE_NOT_FOUND);
        });

        TimelineLearningRecord record = timelineLearningRecordRepository.findByCustomerAndTimeline(customer, timeline).orElseGet(() -> {
            TimelineLearningRecord newRecord = new TimelineLearningRecord(timeline, customer);
            timelineLearningRecordRepository.save(newRecord);
            return newRecord;
        });
        return record;
    }

    private TopicLearningRecord getTopicLearningRecord(Customer customer, String topicTitle) {
        Topic topic = topicRepository.findTopicByTitle(topicTitle).orElseThrow(() -> {
            throw new CustomException(TOPIC_NOT_FOUND);
        });

        TopicLearningRecord record = topicLearningRecordRepository.findByCustomerAndTopic(customer, topic).orElseGet(() -> {
            TopicLearningRecord newRecord = new TopicLearningRecord(topic, customer);
            topicLearningRecordRepository.save(newRecord);
            return newRecord;
        });
        return record;
    }

    public List<String> queryBookmarks(Customer customer) {
        List<Bookmark> bookmarkList = bookmarkRepository.queryBookmarks(customer.getId());
        List<String> titleList = bookmarkList.stream().map(b -> b.getTopic().getTitle()).collect(Collectors.toList());
        return titleList;
    }
}
