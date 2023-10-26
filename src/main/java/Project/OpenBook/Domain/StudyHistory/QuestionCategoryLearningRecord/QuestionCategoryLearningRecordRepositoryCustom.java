package Project.OpenBook.Domain.StudyHistory.QuestionCategoryLearningRecord;

import Project.OpenBook.Domain.Customer.Domain.Customer;

import java.util.List;

public interface QuestionCategoryLearningRecordRepositoryCustom {

    public List<QuestionCategoryLearningRecord> queryQuestionRecords(Customer customer);
}
