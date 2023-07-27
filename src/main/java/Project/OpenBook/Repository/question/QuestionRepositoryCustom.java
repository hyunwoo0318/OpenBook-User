package Project.OpenBook.Repository.question;

import Project.OpenBook.Dto.question.QuestionDto;
import com.querydsl.core.Tuple;

import java.util.List;

public interface QuestionRepositoryCustom {

    public QuestionDto findQuestionById(Long id);


}
