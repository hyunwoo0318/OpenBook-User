package Project.OpenBook.Domain.KeywordLearningRecord.Repo;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.KeywordLearningRecord.Domain.KeywordLearningRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KeywordLearningRecordRepository extends JpaRepository<KeywordLearningRecord, Long> , KeywordLearningRecordRepositoryCustom{

    public List<KeywordLearningRecord> findAllByCustomer(Customer customer);
}
