package Project.OpenBook.Repository.choice;

import Project.OpenBook.Domain.Choice;
import Project.OpenBook.Domain.QChoice;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
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
    public Choice queryRandChoiceByChoice(Long choiceId) {
        QChoice choice1 = new QChoice("choice1");
        return queryFactory.selectFrom(choice)
                .innerJoin(choice1)
                .where(choice1.id.eq(choiceId))
                .where(choice1.topic.eq(choice.topic))
                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
                .limit(1)
                .fetchOne();
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
    public List<Choice> queryRandChoicesByCategory(String categoryName, int num) {
        return queryFactory.selectFrom(choice)
                .where(choice.topic.category.name.eq(categoryName))
                .where(choice.topic.startDate.eq(choice.topic.endDate))
                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
                .orderBy(choice.topic.startDate.asc())
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
    public List<Choice> queryChoicesType2(LocalDate startDate, LocalDate endDate, int num, int interval, String categoryName) {
        List<Choice> choiceList = queryFactory.selectFrom(choice)
                .where(notInDateBetween(startDate, endDate))
                .where(choice.topic.category.name.eq(categoryName))
                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
                .limit(num-1)
                .fetch();
        Choice answerChoice = queryFactory.selectFrom(choice)
                .where(choice.topic.category.name.eq(categoryName))
                .where(choice.topic.startDate.goe(startDate))
                .where(choice.topic.endDate.loe(endDate))
                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
                .limit(1)
                .fetch().get(0);
        choiceList.add(answerChoice);
        return choiceList;
    }
    @Override
    public List<Choice> queryChoicesType3(LocalDate startDate,LocalDate endDate, int num, int interval, String categoryName) {
        List<Choice> choiceList = queryFactory.selectFrom(choice)
                .where(choice.topic.endDate.lt(startDate))
                .where(choice.topic.category.name.eq(categoryName))
                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
                .limit(num-1)
                .fetch();
        Choice answerChoice = queryFactory.selectFrom(choice)
                .where(choice.topic.category.name.eq(categoryName))
                .where(choice.topic.startDate.goe(endDate))
                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
                .limit(1)
                .fetch().get(0);
        choiceList.add(answerChoice);
        return choiceList;
    }

    @Override
    public List<Choice> queryChoicesType4(LocalDate startDate,LocalDate endDate, int num, int interval, String categoryName) {
        List<Choice> choiceList = queryFactory.selectFrom(choice)
                .where(choice.topic.startDate.gt(endDate))
                .where(choice.topic.category.name.eq(categoryName))
                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
                .limit(num-1)
                .fetch();
        Choice answerChoice = queryFactory.selectFrom(choice)
                .where(choice.topic.category.name.eq(categoryName))
                .where(choice.topic.endDate.loe(startDate))
                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
                .limit(1)
                .fetch().get(0);
        return choiceList;
    }

    private BooleanExpression notInDateBetween(LocalDate ansStartDate, LocalDate ansEndDate) {
        return choice.topic.startDate.lt(ansStartDate).or(choice.topic.endDate.gt(ansEndDate));
    }
}
