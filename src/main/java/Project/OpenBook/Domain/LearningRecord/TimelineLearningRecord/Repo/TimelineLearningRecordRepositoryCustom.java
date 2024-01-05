package Project.OpenBook.Domain.LearningRecord.TimelineLearningRecord.Repo;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.LearningRecord.TimelineLearningRecord.Domain.TimelineLearningRecord;
import java.util.List;

public interface TimelineLearningRecordRepositoryCustom {

    public List<TimelineLearningRecord> queryTimelineLearningRecord(Customer customer);

    public List<TimelineLearningRecord> queryTimelineLearningRecordInKeywords(Customer customer,
        List<Long> timelineIdList);
}
