package Project.OpenBook.Domain.Bookmark.Service;

import static Project.OpenBook.Constants.ErrorCode.TOPIC_NOT_FOUND;

import Project.OpenBook.Domain.Bookmark.Dto.BookmarkDto;
import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.LearningRecord.TopicLearningRecord.Domain.TopicLearningRecord;
import Project.OpenBook.Domain.LearningRecord.TopicLearningRecord.Repo.TopicLearningRecordRepository;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import Project.OpenBook.Domain.Topic.Repo.TopicRepository;
import Project.OpenBook.Handler.Exception.CustomException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookmarkService {

  private final TopicRepository topicRepository;

  private final TopicLearningRecordRepository topicLearningRecordRepository;

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
    record.updateBookmark(false);
  }

  private TopicLearningRecord getTopicLearningRecord(Customer customer, String topicTitle) {
    Topic topic =
        topicRepository
            .findTopicByTitle(topicTitle)
            .orElseThrow(() -> new CustomException(TOPIC_NOT_FOUND));

    TopicLearningRecord record =
        topicLearningRecordRepository
            .findByCustomerAndTopic(customer, topic)
            .orElseGet(
                () -> {
                  TopicLearningRecord newRecord = new TopicLearningRecord(topic, customer);
                  topicLearningRecordRepository.save(newRecord);
                  return newRecord;
                });
    return record;
  }

  public Map<Topic, Boolean> queryBookmarks(Customer customer, List<Topic> topicList) {
    Map<Topic, TopicLearningRecord> recordMap =
        topicLearningRecordRepository
            .queryTopicLearningRecordsBookmarked(customer, topicList)
            .stream()
            .collect(Collectors.toMap(TopicLearningRecord::getTopic, record -> record));
    Map<Topic, Boolean> bookmarkMap = new HashMap<>();
    for (Topic topic : topicList) {
      TopicLearningRecord record = recordMap.get(topic);
      if (record == null) {
        record = new TopicLearningRecord(topic, customer);
        topicLearningRecordRepository.save(record);
      }
      bookmarkMap.put(topic, record.getIsBookmarked());
    }
    return bookmarkMap;
  }
}
