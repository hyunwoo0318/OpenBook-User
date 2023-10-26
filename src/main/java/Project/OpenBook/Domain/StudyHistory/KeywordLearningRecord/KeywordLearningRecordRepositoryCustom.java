package Project.OpenBook.Domain.StudyHistory.KeywordLearningRecord;

import Project.OpenBook.Domain.Customer.Domain.Customer;

import java.util.List;

public interface KeywordLearningRecordRepositoryCustom {
    public List<KeywordLearningRecord> queryKeywordLearningRecordsInKeywords(Customer customer, List<Long> keywordIdList);
}
