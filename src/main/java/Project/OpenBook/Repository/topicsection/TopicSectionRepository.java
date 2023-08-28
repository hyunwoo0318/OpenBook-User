package Project.OpenBook.Repository.topicsection;

import Project.OpenBook.Domain.TopicSection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicSectionRepository extends JpaRepository<TopicSection, Long>, TopicSectionRepositoryCustom {
}
