package Project.OpenBook.Domain.ExamQuestion.Repo;

import Project.OpenBook.Domain.ExamQuestion.Domain.ExamQuestion;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static Project.OpenBook.Domain.Description.Domain.QDescription.description;
import static Project.OpenBook.Domain.ExamQuestion.Domain.QExamQuestion.examQuestion;
import static Project.OpenBook.Domain.Topic.Domain.QTopic.topic;


@Repository
@RequiredArgsConstructor
public class ExamQuestionRepositoryCustomImpl implements ExamQuestionRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<ExamQuestion> queryExamQuestion(Integer roundNumber, Integer questionNumber) {
        ExamQuestion findExamQuestion = queryFactory.selectFrom(examQuestion)
                .where(examQuestion.round.number.eq(roundNumber))
                .where(examQuestion.number.eq(questionNumber))
                .fetchOne();
        return Optional.ofNullable(findExamQuestion);
    }

    @Override
    public Optional<ExamQuestion> queryExamQuestionWithDescriptionAndTopic(Integer roundNumber, Integer questionNumber) {
        ExamQuestion findExamQuestion = queryFactory.selectFrom(examQuestion)
                .leftJoin(examQuestion.description,description).fetchJoin()
                .leftJoin(description.topic, topic).fetchJoin()
                .where(examQuestion.round.number.eq(roundNumber))
                .where(examQuestion.number.eq(questionNumber))
                .fetchOne();
        return Optional.ofNullable(findExamQuestion);
    }

    @Override
    public Optional<ExamQuestion> queryExamQuestionWithDescription(Integer roundNumber, Integer questionNumber) {
        ExamQuestion findExamQuestion = queryFactory.selectFrom(examQuestion)
                .leftJoin(examQuestion.description,description).fetchJoin()
                .where(examQuestion.round.number.eq(roundNumber))
                .where(examQuestion.number.eq(questionNumber))
                .fetchOne();
        return Optional.ofNullable(findExamQuestion);
    }

    @Override
    public List<ExamQuestion> queryExamQuestionsForExamQuestionList(Integer roundNumber) {
        return queryFactory.selectFrom(examQuestion).distinct()
                .leftJoin(examQuestion.description, description).fetchJoin()
                .leftJoin(examQuestion.choiceList).fetchJoin()
                .where(examQuestion.round.number.eq(roundNumber))
                .orderBy(examQuestion.number.asc())
                .fetch();
    }

    @Override
    public Optional<ExamQuestion> queryExamQuestion(Long examQuestionId) {
        ExamQuestion findExamQuestion = queryFactory.selectFrom(examQuestion).distinct()
                .leftJoin(examQuestion.description, description).fetchJoin()
                .leftJoin(examQuestion.choiceList).fetchJoin()
                .where(examQuestion.id.eq(examQuestionId))
                .fetchOne();
        return Optional.ofNullable(findExamQuestion);
    }
}
