package Project.OpenBook.Domain.ExamQuestion.Repo;

import Project.OpenBook.Domain.ExamQuestion.Domain.ExamQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamQuestionRepository extends JpaRepository<ExamQuestion, Long>, ExamQuestionRepositoryCustom {
}
