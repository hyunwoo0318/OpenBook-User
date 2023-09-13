package Project.OpenBook.Domain.Question.Repository;

import Project.OpenBook.Domain.Question.Domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long>, QuestionRepositoryCustom {
}
