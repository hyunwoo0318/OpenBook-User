package Project.OpenBook.Domain.Topic.TopicPrimaryDate.Repository;

import Project.OpenBook.Domain.Topic.TopicPrimaryDate.Domain.TopicPrimaryDate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicPrimaryDateRepository extends JpaRepository<TopicPrimaryDate, Long>, TopicPrimaryDateRepositoryCustom {
}
