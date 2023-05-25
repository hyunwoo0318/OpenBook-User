package Project.OpenBook.Repository.dupdate;

import Project.OpenBook.Domain.DupDate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DupDateRepository extends JpaRepository<DupDate, Long>, DupDateRepositoryCustom {
}
