package Project.OpenBook.Domain.Topic.PrimaryDate.Repository;

import Project.OpenBook.Domain.Topic.PrimaryDate.Domain.PrimaryDate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrimaryDateRepository extends JpaRepository<PrimaryDate, Long> {
}
