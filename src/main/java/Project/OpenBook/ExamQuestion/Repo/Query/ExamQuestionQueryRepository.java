package Project.OpenBook.ExamQuestion.Repo.Query;

import Project.OpenBook.ExamQuestion.dto.ExamQuestionDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface ExamQuestionQueryRepository  {

    public Optional<ExamQuestionDto> queryExamQuestionDto(Integer roundNumber, Integer questionNumber);

}
