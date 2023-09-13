package Project.OpenBook.Domain.Question.Service;

import Project.OpenBook.Domain.Question.Dto.QuestionDto;

public interface QuestionFactory {
    public abstract QuestionDto getQuestion(String topicTitle);

}
