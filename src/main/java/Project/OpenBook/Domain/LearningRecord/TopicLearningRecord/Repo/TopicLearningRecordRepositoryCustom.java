package Project.OpenBook.Domain.LearningRecord.TopicLearningRecord.Repo;

import Project.OpenBook.Domain.LearningRecord.TopicLearningRecord.Domain.TopicLearningRecord;
import Project.OpenBook.Domain.Customer.Domain.Customer;

import java.util.List;

public interface TopicLearningRecordRepositoryCustom {
    public List<TopicLearningRecord> queryTopicLearningRecordsInKeywords(Customer customer, List<Long> topicIdList);

    public List<TopicLearningRecord> queryTopicLearningRecordsBookmarked(Customer customer);

    public List<TopicLearningRecord> queryTopicLearningRecordsInQuestionCategory(Customer customer, Long questionCategoryId);
}
