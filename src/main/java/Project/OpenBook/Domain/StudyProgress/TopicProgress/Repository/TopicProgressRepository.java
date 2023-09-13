package Project.OpenBook.Domain.StudyProgress.TopicProgress.Repository;

import Project.OpenBook.Domain.StudyProgress.TopicProgress.Domain.TopicProgress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicProgressRepository extends TopicProgressRepositoryCustom, JpaRepository<TopicProgress, Long> {
}
