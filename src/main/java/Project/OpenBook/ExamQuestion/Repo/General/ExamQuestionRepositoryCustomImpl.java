package Project.OpenBook.ExamQuestion.Repo.General;

import Project.OpenBook.Domain.QDescription;
import Project.OpenBook.ExamQuestion.ExamQuestion;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static Project.OpenBook.Domain.QDescription.description;
import static Project.OpenBook.ExamQuestion.QExamQuestion.examQuestion;

@Repository
@RequiredArgsConstructor
public class ExamQuestionRepositoryCustomImpl implements ExamQuestionRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<ExamQuestion> queryExamQuestion(Integer roundNumber, Integer examNumber) {
        ExamQuestion findExamQuestion = queryFactory.selectFrom(examQuestion)
                .where(examQuestion.round.number.eq(roundNumber))
                .where(examQuestion.number.eq(examNumber))
                .fetchOne();
        return Optional.ofNullable(findExamQuestion);
    }

    @Override
    public Optional<ExamQuestion> queryExamQuestionWithDescription(Integer roundNumber, Integer examNumber) {
        ExamQuestion findExamQuestion = queryFactory.selectFrom(examQuestion)
                .leftJoin(description).on(examQuestion.description.eq(description))
                .where(examQuestion.round.number.eq(roundNumber))
                .where(examQuestion.number.eq(examNumber))
                .fetchOne();
        return Optional.ofNullable(findExamQuestion);
    }

    @Override
    public List<ExamQuestion> queryExamQuestions(Integer roundNumber) {
        return queryFactory.selectFrom(examQuestion)
                .where(examQuestion.round.number.eq(roundNumber))
                .fetch();
    }
}
