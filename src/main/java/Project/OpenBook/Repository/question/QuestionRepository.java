package Project.OpenBook.Repository.question;

import Project.OpenBook.Domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long>, QuestionRepositoryCustom {
}
