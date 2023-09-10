package Project.OpenBook.ExamQuestion.Repo.Query;

import Project.OpenBook.ExamQuestion.Controller.dto.ExamQuestionDto;

import java.util.Optional;

public interface ExamQuestionQueryRepository  {

    public Optional<ExamQuestionDto> queryExamQuestionDto(Integer roundNumber, Integer questionNumber);

}
