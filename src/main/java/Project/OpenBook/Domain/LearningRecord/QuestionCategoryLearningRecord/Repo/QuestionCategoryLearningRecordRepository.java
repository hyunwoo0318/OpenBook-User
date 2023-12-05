package Project.OpenBook.Domain.LearningRecord.QuestionCategoryLearningRecord.Repo;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.LearningRecord.QuestionCategoryLearningRecord.Domain.QuestionCategoryLearningRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionCategoryLearningRecordRepository
        extends JpaRepository<QuestionCategoryLearningRecord,Long>, QuestionCategoryLearningRecordRepositoryCustom {


    public void deleteAllByCustomer(Customer customer);
}
