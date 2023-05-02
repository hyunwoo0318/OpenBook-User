package Project.OpenBook.Repository.choice;

import Project.OpenBook.Domain.Choice;
import Project.OpenBook.Domain.Topic;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.MathExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static Project.OpenBook.Domain.QChoice.choice;

@Repository
@RequiredArgsConstructor
public class ChoiceRepositoryCustomImpl implements ChoiceRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Choice> queryChoicesById(List<Long> choiceIdList) {
        return queryFactory.selectFrom(choice)
                .where(choice.id.in(choiceIdList))
                .fetch();
    }

    @Override
    public Choice queryRandChoiceByTopic(String topicTitle) {
        return queryFactory.selectFrom(choice)
                .where(choice.topic.title.eq(topicTitle))
                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
                .limit(1)
                .fetchOne();
    }

    @Override
    public List<Choice> queryRandChoicesByCategory(String exceptTopicTitle, String categoryName, int num) {
        return queryFactory.selectFrom(choice)
                .where(choice.topic.category.name.eq(categoryName))
                .where(choice.topic.title.ne(exceptTopicTitle))
                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
                .limit(num)
                .fetch();
    }

    @Override
    public Choice queryRandChoiceByTime(LocalDate startDate, LocalDate endDate) {
        return queryFactory.selectFrom(choice)
                .where(choice.topic.startDate.gt(startDate).or(choice.topic.endDate.lt(endDate)))
                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
                .limit(1)
                .fetchOne();

    }

    @Override
    public List<Choice> queryChoiceByTopicTitle(String topicTitle) {
        return queryFactory.selectFrom(choice)
                .where(choice.topic.title.eq(topicTitle))
                .fetch();
    }

    @Override
    public List<Choice> queryRandChoicesByTime(LocalDate startDate, LocalDate endDate, int num, int interval, String categoryName) {
        return queryFactory.selectFrom(choice)
                .where(notInDate(startDate, endDate))
                .where(choice.topic.category.name.eq(categoryName))
                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
                .limit(num)
                .fetch();
    }

    private BooleanExpression notInDate(LocalDate ansStartDate, LocalDate ansEndDate) {
        return choice.topic.endDate.lt(ansStartDate).or(choice.topic.endDate.gt(ansStartDate));
    }
}
