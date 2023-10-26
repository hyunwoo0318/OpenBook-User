package Project.OpenBook.Domain.TopicLearningRecord.Repo;

import Project.OpenBook.Domain.TopicLearningRecord.Domain.TopicLearningRecord;
import Project.OpenBook.Domain.Customer.Domain.Customer;

import java.util.List;

public interface TopicLearningRecordRepositoryCustom {
    public List<TopicLearningRecord> queryTopicLearningRecordsInKeywords(Customer customer, List<Long> keywordIdList);
}
