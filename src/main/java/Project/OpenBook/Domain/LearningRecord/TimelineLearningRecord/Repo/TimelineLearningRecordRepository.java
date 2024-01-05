package Project.OpenBook.Domain.LearningRecord.TimelineLearningRecord.Repo;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.LearningRecord.TimelineLearningRecord.Domain.TimelineLearningRecord;
import Project.OpenBook.Domain.Timeline.Domain.Timeline;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimelineLearningRecordRepository extends
    JpaRepository<TimelineLearningRecord, Long>, TimelineLearningRecordRepositoryCustom {

    public Optional<TimelineLearningRecord> findByCustomerAndTimeline(Customer customer,
        Timeline timeline);

    public List<TimelineLearningRecord> findAllByCustomer(Customer customer);


    public void deleteAllByCustomer(Customer customer);
}
