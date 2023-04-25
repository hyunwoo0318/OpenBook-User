package Project.OpenBook.Repository;

import Project.OpenBook.Dto.QuestionDto;

public interface QuestionRepositoryCustom {

    public QuestionDto findQuestionById(Long id);
}
