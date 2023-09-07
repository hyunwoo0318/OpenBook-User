package Project.OpenBook.ExamQuestion.Repo.General;

import Project.OpenBook.ExamQuestion.ExamQuestion;

import java.util.List;
import java.util.Optional;

public interface ExamQuestionRepositoryCustom {

    public Optional<ExamQuestion> queryExamQuestion(Integer roundNumber, Integer examNumber);

    public Optional<ExamQuestion> queryExamQuestionWithDescription(Integer roundNumber, Integer examNumber);

    public List<ExamQuestion> queryExamQuestions(Integer roundNumber);

}
