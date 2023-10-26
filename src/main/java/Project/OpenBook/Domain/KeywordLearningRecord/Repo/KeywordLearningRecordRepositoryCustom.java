package Project.OpenBook.Domain.KeywordLearningRecord.Repo;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.KeywordLearningRecord.Domain.KeywordLearningRecord;

import java.util.List;

public interface KeywordLearningRecordRepositoryCustom {
    public List<KeywordLearningRecord> queryKeywordLearningRecordsInKeywords(Customer customer, List<Long> keywordIdList);
}
