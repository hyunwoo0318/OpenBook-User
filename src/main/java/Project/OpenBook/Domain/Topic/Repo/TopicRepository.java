package Project.OpenBook.Domain.Topic.Repo;

import Project.OpenBook.Domain.Category.Domain.Category;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long>, TopicRepositoryCustom {

    public Optional<Topic> findTopicByTitle(String title);
}
