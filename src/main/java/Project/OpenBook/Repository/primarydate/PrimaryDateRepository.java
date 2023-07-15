package Project.OpenBook.Repository.primarydate;

import Project.OpenBook.Domain.PrimaryDate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrimaryDateRepository extends JpaRepository<PrimaryDate, Long>, PrimaryDateRepositoryCustom {


}
