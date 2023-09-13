package Project.OpenBook.Domain.Keyword.Repository;

import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeywordRepository extends JpaRepository<Keyword, Long>, KeywordRepositoryCustom {

}
