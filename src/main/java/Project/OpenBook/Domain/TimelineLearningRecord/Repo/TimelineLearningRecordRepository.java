package Project.OpenBook.Domain.TimelineLearningRecord.Repo;

import Project.OpenBook.Domain.TimelineLearningRecord.Domain.TimelineLearningRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimelineLearningRecordRepository extends JpaRepository<TimelineLearningRecord, Long>, TimelineLearningRecordRepositoryCustom {
}
