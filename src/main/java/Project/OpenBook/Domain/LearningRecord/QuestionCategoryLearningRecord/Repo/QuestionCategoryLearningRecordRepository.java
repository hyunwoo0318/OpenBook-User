package Project.OpenBook.Domain.LearningRecord.QuestionCategoryLearningRecord.Repo;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.LearningRecord.QuestionCategoryLearningRecord.Domain.QuestionCategoryLearningRecord;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionCategoryLearningRecordRepository
    extends JpaRepository<QuestionCategoryLearningRecord, Long>,
    QuestionCategoryLearningRecordRepositoryCustom {


    public List<QuestionCategoryLearningRecord> findAllByCustomer(Customer customer);

    public void deleteAllByCustomer(Customer customer);
}
