package Project.OpenBook.Domain.TopicLearningRecord.Repo;

import Project.OpenBook.Domain.TopicLearningRecord.Domain.TopicLearningRecord;
import Project.OpenBook.Domain.Customer.Domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TopicLearningRecordRepository extends JpaRepository<TopicLearningRecord, Long> , TopicLearningRecordRepositoryCustom {

    public List<TopicLearningRecord> findAllByCustomer(Customer customer);
}
