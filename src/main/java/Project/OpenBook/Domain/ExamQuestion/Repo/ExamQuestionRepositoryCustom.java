package Project.OpenBook.Domain.ExamQuestion.Repo;

import Project.OpenBook.Domain.ExamQuestion.Domain.ExamQuestion;

import java.util.List;
import java.util.Optional;

public interface ExamQuestionRepositoryCustom {

    public Optional<ExamQuestion> queryExamQuestion(Integer roundNumber, Integer questionNumber);

    public Optional<ExamQuestion> queryExamQuestionWithDescriptionAndTopic(Integer roundNumber, Integer questionNumber);

    public Optional<ExamQuestion> queryExamQuestionWithDescription(Integer roundNumber, Integer questionNumber);

    public List<ExamQuestion> queryExamQuestionsForExamQuestionList(Integer roundNumber);

    public Optional<ExamQuestion> queryExamQuestion(Long examQuestionId);


}
