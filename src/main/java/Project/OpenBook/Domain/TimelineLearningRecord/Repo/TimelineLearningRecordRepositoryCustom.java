package Project.OpenBook.Domain.TimelineLearningRecord.Repo;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.TimelineLearningRecord.Domain.TimelineLearningRecord;

import java.util.List;

public interface TimelineLearningRecordRepositoryCustom {

    public List<TimelineLearningRecord> queryTimelineLearningRecord(Customer customer);
}