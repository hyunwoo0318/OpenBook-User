package Project.OpenBook.Domain.LearningRecord.KeywordLearningRecord.Repo;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.LearningRecord.KeywordLearningRecord.Domain.KeywordLearningRecord;
import java.util.List;

public interface KeywordLearningRecordRepositoryCustom {

    public List<KeywordLearningRecord> queryKeywordLearningRecordsInKeywords(Customer customer,
        List<Long> keywordIdList);

    public List<KeywordLearningRecord> queryKeywordLearningRecordsInQuestionCategory(
        Customer customer, Long questionCategoryId);


}
