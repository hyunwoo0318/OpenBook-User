package Project.OpenBook.Repository.Question;

import Project.OpenBook.Domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long>, QuestionRepositoryCustom {
}
