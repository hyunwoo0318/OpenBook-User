package Project.OpenBook.Repository.topickeyword;

import Project.OpenBook.Domain.TopicKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicKeywordRepository extends JpaRepository<TopicKeyword, Long>, TopicKeywordRepositoryCustom {
}
