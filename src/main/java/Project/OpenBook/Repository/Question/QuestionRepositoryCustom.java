package Project.OpenBook.Repository.Question;

import Project.OpenBook.Dto.question.QuestionDto;

public interface QuestionRepositoryCustom {

    public QuestionDto findQuestionById(Long id);
}
