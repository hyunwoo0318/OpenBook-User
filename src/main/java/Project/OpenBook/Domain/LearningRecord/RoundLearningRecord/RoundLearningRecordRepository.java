package Project.OpenBook.Domain.LearningRecord.RoundLearningRecord;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.Round.Domain.Round;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoundLearningRecordRepository extends JpaRepository<RoundLearningRecord, Long>, RoundLearningRecordRepositoryCustom {

    public Optional<RoundLearningRecord> findByCustomerAndRound(Customer customer, Round round);

    public void deleteAllByCustomer(Customer customer);
}
