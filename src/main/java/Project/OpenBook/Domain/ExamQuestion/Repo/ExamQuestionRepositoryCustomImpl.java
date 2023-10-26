package Project.OpenBook.Domain.ExamQuestion.Repo;

import Project.OpenBook.Domain.ExamQuestion.Domain.ExamQuestion;
import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static Project.OpenBook.Domain.Description.Domain.QDescription.description;
import static Project.OpenBook.Domain.ExamQuestion.Domain.QExamQuestion.examQuestion;
import static Project.OpenBook.Domain.Topic.Domain.QTopic.topic;
import static com.querydsl.core.group.GroupBy.groupBy;


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
    public List<ExamQuestion> queryExamQuestionsWithDescriptionAndTopic(Integer roundNumber) {
        return queryFactory.selectFrom(examQuestion)
                .leftJoin(examQuestion.description, description).fetchJoin()
                .leftJoin(description.topic, topic).fetchJoin()
                .where(examQuestion.round.number.eq(roundNumber))
                .orderBy(examQuestion.number.asc())
                .fetch();
    }


}
