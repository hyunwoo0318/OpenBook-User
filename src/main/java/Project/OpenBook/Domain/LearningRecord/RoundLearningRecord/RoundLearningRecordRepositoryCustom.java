package Project.OpenBook.Domain.LearningRecord.RoundLearningRecord;

import Project.OpenBook.Domain.Customer.Domain.Customer;

import java.util.List;

public interface RoundLearningRecordRepositoryCustom {

    public List<RoundLearningRecord> queryRoundLearningRecord(Customer customer);
}
