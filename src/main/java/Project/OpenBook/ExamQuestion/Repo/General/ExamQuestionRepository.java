package Project.OpenBook.ExamQuestion.Repo.General;

import Project.OpenBook.ExamQuestion.ExamQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamQuestionRepository extends JpaRepository<ExamQuestion, Long>, ExamQuestionRepositoryCustom {
}
