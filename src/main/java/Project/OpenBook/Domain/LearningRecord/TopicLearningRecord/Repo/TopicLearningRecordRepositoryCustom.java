package Project.OpenBook.Domain.LearningRecord.TopicLearningRecord.Repo;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.LearningRecord.TopicLearningRecord.Domain.TopicLearningRecord;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import java.util.List;

public interface TopicLearningRecordRepositoryCustom {

    public List<TopicLearningRecord> queryTopicLearningRecordsInKeyword(Customer customer,
        List<Long> topicIdList);

    public List<TopicLearningRecord> queryTopicLearningRecordsBookmarked(Customer customer,
        List<Topic> topicList);

    public List<TopicLearningRecord> queryTopicLearningRecordsBookmarked(Customer customer);

    public List<TopicLearningRecord> queryTopicLearningRecordsInQuestionCategory(Customer customer,
        Long questionCategoryId);
}
