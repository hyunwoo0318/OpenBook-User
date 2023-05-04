package Project.OpenBook.Repository.question;

import Project.OpenBook.Dto.question.QuestionDto;

public interface QuestionRepositoryCustom {

    public QuestionDto findQuestionById(Long id);
}
