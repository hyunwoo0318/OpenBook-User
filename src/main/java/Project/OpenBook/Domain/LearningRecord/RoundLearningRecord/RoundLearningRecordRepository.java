package Project.OpenBook.Domain.LearningRecord.RoundLearningRecord;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.Round.Domain.Round;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoundLearningRecordRepository extends JpaRepository<RoundLearningRecord, Long>,
    RoundLearningRecordRepositoryCustom {

    public Optional<RoundLearningRecord> findByCustomerAndRound(Customer customer, Round round);

    public List<RoundLearningRecord> findAllByCustomer(Customer customer);


    public void deleteAllByCustomer(Customer customer);
}
