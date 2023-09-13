package Project.OpenBook.Domain.PrimaryDate.Repository;

import Project.OpenBook.Domain.PrimaryDate.Domain.PrimaryDate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrimaryDateRepository extends JpaRepository<PrimaryDate, Long>, PrimaryDateRepositoryCustom {


}
