package Project.OpenBook.Domain.LearningRecord.QuestionCategoryLearningRecord.Repo;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.LearningRecord.QuestionCategoryLearningRecord.Domain.QuestionCategoryLearningRecord;
import java.util.List;

public interface QuestionCategoryLearningRecordRepositoryCustom {

    public List<QuestionCategoryLearningRecord> queryQuestionRecords(Customer customer);

    public List<QuestionCategoryLearningRecord> queryQuestionRecordsInKeywords(Customer customer,
        List<Long> questionCategoryIdList);

    public QuestionCategoryLearningRecord queryQuestionCategoryLowScore(Customer customer);

    public List<QuestionCategoryLearningRecord> queryQuestionRecordsForInit();
}
