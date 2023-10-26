package Project.OpenBook.Domain.StudyHistory.KeywordLearningRecord;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KeywordLearningRecordRepository extends JpaRepository<KeywordLearningRecord, Long> , KeywordLearningRecordRepositoryCustom{

    public List<KeywordLearningRecord> findAllByCustomer(Customer customer);
}
