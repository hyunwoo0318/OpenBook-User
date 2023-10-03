package Project.OpenBook.Domain.Choice.Repository;

import Project.OpenBook.Domain.Category.Domain.Category;
import Project.OpenBook.Domain.Choice.Domain.Choice;
import Project.OpenBook.Domain.*;
import Project.OpenBook.Domain.Choice.Domain.QChoice;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static Project.OpenBook.Domain.Category.Domain.QCategory.category;
import static Project.OpenBook.Domain.Choice.Domain.QChoice.choice;
import static Project.OpenBook.Domain.Topic.Domain.QTopic.topic;

@Repository
@RequiredArgsConstructor
public class ChoiceRepositoryCustomImpl implements ChoiceRepositoryCustom{

    private final JPAQueryFactory queryFactory;


//    @Override
//    public List<Choice> queryChoicesById(List<Long> choiceIdList) {
//        return queryFactory.selectFrom(choice)
//                .leftJoin(topic).on(choice.topic.eq(topic)).fetchJoin()
//                .where(choice.id.in(choiceIdList))
//                .fetch();
//    }
//
//    @Override
//    public Choice queryChoiceByContent(String content) {
//        return queryFactory.selectFrom(choice)
//                .where(choice.content.eq(content))
//                .fetchOne();
//    }
//
//    @Override
//    public Choice queryRandChoiceByChoice(Long choiceId) {
//        QChoice choice1 = new QChoice("choice1");
//        return queryFactory.select(choice)
//                .from(choice, choice1)
//                .where(choice1.id.eq(choiceId))
//                .where(choice1.topic.eq(choice.topic))
//                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
//                .limit(1)
//                .fetchOne();
//    }
//
////    @Override
////    public Choice queryRandChoiceByTopic(Long topicId, Long descriptionId) {
////        return queryFactory.selectFrom(choice)
////                .where(choice.topic.id.eq(topicId))
////                .where(choice.id.notIn(
////                        JPAExpressions.select(dupContent.choice.id)
////                                .from(dupContent)
////                                .where(dupContent.description.id.eq(descriptionId))
////                ))
////                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
////                .limit(1)
////                .fetchOne();
////    }
//
//    @Override
//    public List<Choice> queryRandChoicesByCategory(String exceptTopicTitle, String categoryName, int num) {
//        return queryFactory.selectFrom(choice)
//                .where(choice.topic.category.name.eq(categoryName))
//                .where(choice.topic.title.ne(exceptTopicTitle))
//                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
//                .limit(num)
//                .fetch();
//    }
//
//    @Override
//    public List<Choice> queryRandChoicesByCategory(String categoryName, int num) {
//        return queryFactory.selectFrom(choice)
//                .where(choice.topic.category.name.eq(categoryName))
//                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
//                .limit(num)
//                .fetch();
//    }
//
//    @Override
//    public Choice queryRandChoiceByTime(Integer startDate, Integer endDate) {
//        return queryFactory.selectFrom(choice)
//                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
//                .limit(1)
//                .fetchOne();
//
//    }
//
//    @Override
//    public List<Choice> queryChoiceByTopicTitle(String topicTitle) {
//        return queryFactory.selectFrom(choice)
//                .where(choice.topic.title.eq(topicTitle))
//                .fetch();
//    }
//
//    @Override
//    public List<Choice> queryChoicesType2(Topic answerTopic, Topic descriptionTopic, int num, int interval, String categoryName) {
//        List<Choice> choiceList = queryFactory.selectFrom(choice)
//                .where(choice.topic.category.name.eq(categoryName))
////                .where(choice.topic.endDate.lt(descriptionTopic.getStartDate()))
////                .where(choice.topic.startDate.gt(descriptionTopic.getEndDate()))
//                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
//                .limit(num-1)
//                .fetch();
//        Choice answerChoice = queryFactory.selectFrom(choice)
//                .where(choice.topic.eq(answerTopic))
//                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
//                .limit(1)
//                .fetchOne();
//        choiceList.add(answerChoice);
//        return choiceList;
//    }
//    @Override
//    public List<Choice> queryChoicesType3(String topicTitle, Integer startDate,Integer endDate, int num, int interval, String categoryName) {
//        List<Choice> choiceList = queryFactory.selectFrom(choice)
//                .where(choice.topic.category.name.eq(categoryName))
//                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
//                .limit(num-1)
//                .fetch();
//        Choice answerChoice = queryFactory.selectFrom(choice)
//                .where(choice.topic.title.ne(topicTitle))
//                .where(choice.topic.category.name.eq(categoryName))
//                .where(choice.topic.startDate.goe(endDate))
//                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
//                .limit(1)
//                .fetchOne();
//        choiceList.add(answerChoice);
//        return choiceList;
//    }
//
//    @Override
//    public List<Choice> queryChoicesType4(String topicTitle, Integer startDate,Integer endDate, int num, int interval, String categoryName) {
//        List<Choice> choiceList = queryFactory.selectFrom(choice)
//                .where(choice.topic.startDate.gt(endDate))
//                .where(choice.topic.category.name.eq(categoryName))
//                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
//                .limit(num-1)
//                .fetch();
//        Choice answerChoice = queryFactory.selectFrom(choice)
//                .where(choice.topic.title.ne(topicTitle))
//                .where(choice.topic.category.name.eq(categoryName))
//                .where(choice.topic.endDate.loe(startDate))
//                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
//                .limit(1)
//                .fetchOne();
//        choiceList.add(answerChoice);
//        return choiceList;
//    }
//
//    @Override
//    public Category queryCategoryByChoice(Long choiceId) {
//        return queryFactory.select(category)
//                .from(category, choice, topic)
//                .where(choice.topic.id.eq(topic.id))
//                .where(topic.category.id.eq(category.id))
//                .where(choice.id.eq(choiceId))
//                .fetchOne();
//    }
//
//    private BooleanExpression notInDateBetween(Integer ansStartDate, Integer ansEndDate) {
//        return choice.topic.endDate.lt(ansStartDate).or(choice.topic.startDate.gt(ansEndDate));
//    }


}
