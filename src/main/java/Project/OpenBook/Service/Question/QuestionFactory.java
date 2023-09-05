package Project.OpenBook.Service.Question;

import Project.OpenBook.Dto.question.QuestionDto;

import java.util.List;

public interface QuestionFactory {
    public abstract QuestionDto getQuestion(String topicTitle);

}
