package Project.OpenBook.Domain.ExamQuestion.Repo.Query;

import Project.OpenBook.Domain.ExamQuestion.Controller.dto.ExamQuestionDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static Project.OpenBook.Domain.QChoice.choice;
import static Project.OpenBook.Domain.QDescription.description;
import static Project.OpenBook.ExamQuestion.Domain.QExamQuestion.examQuestion;

@Repository
@RequiredArgsConstructor
public class ExamQuestionQueryRepositoryImpl implements ExamQuestionQueryRepository{
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<ExamQuestionDto> queryExamQuestionDto(Integer roundNumber, Integer questionNumber) {
        queryFactory.select(examQuestion, choice, description)
                .fetchOne();
        return Optional.ofNullable(null);
    }
}
