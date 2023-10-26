package Project.OpenBook.Domain.Timeline.Repo;

import Project.OpenBook.Domain.Timeline.Domain.Timeline;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimelineRepository extends JpaRepository<Timeline, Long>, TimelineRepositoryCustom {

}
