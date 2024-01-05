package Project.OpenBook.Domain.LearningRecord.KeywordLearningRecord.Repo;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.LearningRecord.KeywordLearningRecord.Domain.KeywordLearningRecord;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeywordLearningRecordRepository extends JpaRepository<KeywordLearningRecord, Long>,
    KeywordLearningRecordRepositoryCustom {

    public List<KeywordLearningRecord> findAllByCustomer(Customer customer);


}
