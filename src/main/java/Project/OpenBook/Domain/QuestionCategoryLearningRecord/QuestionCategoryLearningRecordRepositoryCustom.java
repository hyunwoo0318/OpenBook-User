package Project.OpenBook.Domain.QuestionCategoryLearningRecord;

import Project.OpenBook.Domain.Customer.Domain.Customer;

import java.util.List;

public interface QuestionCategoryLearningRecordRepositoryCustom {

    public List<QuestionCategoryLearningRecord> queryQuestionRecords(Customer customer);
}
