package Project.OpenBook.Domain.LearningRecord.TopicLearningRecord.Repo;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.LearningRecord.TopicLearningRecord.Domain.TopicLearningRecord;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicLearningRecordRepository extends JpaRepository<TopicLearningRecord, Long>,
    TopicLearningRecordRepositoryCustom {

    public List<TopicLearningRecord> findAllByCustomer(Customer customer);

    public List<TopicLearningRecord> findAllByCustomerAndTopicIn(Customer customer,
        List<Topic> topicList);

    public Optional<TopicLearningRecord> findByCustomerAndTopic(Customer customer, Topic topic);

    public void deleteAllByCustomer(Customer customer);
}
