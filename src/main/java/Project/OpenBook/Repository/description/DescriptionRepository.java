package Project.OpenBook.Repository.description;

import Project.OpenBook.Domain.Description;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DescriptionRepository extends JpaRepository<Description, Long>, DescriptionRepositoryCustom {
}
