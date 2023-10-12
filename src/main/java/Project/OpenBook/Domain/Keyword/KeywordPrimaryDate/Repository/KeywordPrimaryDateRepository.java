package Project.OpenBook.Domain.Keyword.KeywordPrimaryDate.Repository;

import Project.OpenBook.Domain.Keyword.KeywordPrimaryDate.Domain.KeywordPrimaryDate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeywordPrimaryDateRepository extends JpaRepository<KeywordPrimaryDate, Long>, KeywordPrimaryDateRepositoryCustom {
}
