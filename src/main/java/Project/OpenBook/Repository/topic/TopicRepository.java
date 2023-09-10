package Project.OpenBook.Repository.topic;

import Project.OpenBook.Domain.Category;
import Project.OpenBook.Chapter.Domain.Chapter;
import Project.OpenBook.Domain.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long>, TopicRepositoryCustom {

    public Optional<Topic> findTopicByTitle(String title);

    public List<Topic> findAllByChapter(Chapter chapter);

    public List<Topic> findAllByCategory(Category category);
}
