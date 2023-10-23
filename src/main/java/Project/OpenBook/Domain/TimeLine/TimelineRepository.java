package Project.OpenBook.Domain.TimeLine;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TimelineRepository extends JpaRepository<Timeline, Long>, TimelineRepositoryCustom {

}
