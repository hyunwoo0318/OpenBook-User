package Project.OpenBook.Repository.topickeyword;

import Project.OpenBook.Domain.Keyword;
import Project.OpenBook.Domain.Topic;
import Project.OpenBook.Domain.TopicKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TopicKeywordRepository extends JpaRepository<TopicKeyword, Long>, TopicKeywordRepositoryCustom {

    public Optional<TopicKeyword> findByTopicAndKeyword(Topic topic, Keyword keyword);
}
