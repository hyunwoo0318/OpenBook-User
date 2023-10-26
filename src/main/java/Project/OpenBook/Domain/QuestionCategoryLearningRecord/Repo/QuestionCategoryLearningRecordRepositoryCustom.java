package Project.OpenBook.Domain.QuestionCategoryLearningRecord.Repo;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.QuestionCategoryLearningRecord.Domain.QuestionCategoryLearningRecord;

import java.util.List;

public interface QuestionCategoryLearningRecordRepositoryCustom {

    public List<QuestionCategoryLearningRecord> queryQuestionRecords(Customer customer);
}
