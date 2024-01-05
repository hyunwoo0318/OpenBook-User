package Project.OpenBook.Domain.LearningRecord.RoundLearningRecord;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import java.util.List;
import java.util.Optional;

public interface RoundLearningRecordRepositoryCustom {

    public List<RoundLearningRecord> queryRoundLearningRecord(Customer customer);

    public Optional<RoundLearningRecord> queryRoundLearningRecord(Customer customer,
        Integer roundNumber);
}
