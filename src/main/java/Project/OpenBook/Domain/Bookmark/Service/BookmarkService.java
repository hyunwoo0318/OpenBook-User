package Project.OpenBook.Domain.Bookmark.Service;

import Project.OpenBook.Domain.Bookmark.Domain.Bookmark;
import Project.OpenBook.Domain.Bookmark.Dto.BookmarkDto;
import Project.OpenBook.Domain.Bookmark.Repository.BookmarkRepository;
import Project.OpenBook.Domain.Chapter.Domain.Chapter;
import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import Project.OpenBook.Domain.LearningRecord.TimelineLearningRecord.Repo.TimelineLearningRecordRepository;
import Project.OpenBook.Domain.LearningRecord.TopicLearningRecord.Domain.TopicLearningRecord;
import Project.OpenBook.Domain.LearningRecord.TopicLearningRecord.Repo.TopicLearningRecordRepository;
import Project.OpenBook.Domain.Timeline.Repo.TimelineRepository;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import Project.OpenBook.Domain.Topic.Repo.TopicRepository;
import Project.OpenBook.Handler.Exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        TopicLearningRecord record = getTopicLearningRecord(customer, topicTitle);
        record.updateBookmark(true);
    }

    @Transactional
    public void deleteBookmark(Customer customer, BookmarkDto dto) {
        String topicTitle = dto.getTopicTitle();

        TopicLearningRecord record = getTopicLearningRecord(customer, topicTitle);
        record.updateBookmark(true);
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
        Map<Chapter, Bookmark> bookmarkMap = bookmarkRepository.queryBookmarks(customer.getId()).stream()
                .collect(Collectors.toMap(b -> b.getTopic().getChapter(), b -> b));

        for (Chapter chapter : bookmarkMap.keySet()) {
            Bookmark bookmark = bookmarkMap.get(chapter);
            Topic topic = bookmark.getTopic();
            List<Keyword> keywordList = topic.getKeywordList();


        }

        return null;
    }
}
