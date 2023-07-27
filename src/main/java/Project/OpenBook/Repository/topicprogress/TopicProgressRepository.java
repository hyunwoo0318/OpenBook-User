package Project.OpenBook.Repository.topicprogress;

import Project.OpenBook.Domain.TopicProgress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicProgressRepository extends TopicProgressRepositoryCustom, JpaRepository<TopicProgress, Long> {
}
