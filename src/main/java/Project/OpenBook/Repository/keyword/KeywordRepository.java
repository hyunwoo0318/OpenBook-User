package Project.OpenBook.Repository.keyword;

import Project.OpenBook.Domain.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KeywordRepository extends JpaRepository<Keyword, Long>, KeywordRepositoryCustom {

    public Optional<Keyword> findByName(String name);
}
