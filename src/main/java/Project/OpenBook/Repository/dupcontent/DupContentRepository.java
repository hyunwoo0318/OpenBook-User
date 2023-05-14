package Project.OpenBook.Repository.dupcontent;

import Project.OpenBook.Domain.DupContent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DupContentRepository extends JpaRepository<DupContent, Long>, DupContentRepositoryCustom {
}
