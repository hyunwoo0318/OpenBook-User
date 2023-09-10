package Project.OpenBook.Repository.topic;

import Project.OpenBook.Domain.*;
import com.querydsl.core.Tuple;
import com.querydsl.core.group.Group;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static Project.OpenBook.Domain.QCategory.category;
import static Project.OpenBook.Domain.QChoice.choice;
import static Project.OpenBook.Domain.QDescription.description;
import static Project.OpenBook.Domain.QKeyword.keyword;
import static Project.OpenBook.Domain.QPrimaryDate.primaryDate;
import static Project.OpenBook.Domain.QSentence.sentence;
import static Project.OpenBook.Domain.QTopic.topic;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;


@Repository
@RequiredArgsConstructor
public class TopicRepositoryCustomImpl implements TopicRepositoryCustom{

    private final JPAQueryFactory queryFactory;


    @Override
    public Topic queryRandTopicByCategory(String categoryName) {
         return queryFactory.selectFrom(topic)
                .where(topic.category.name.eq(categoryName))
                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
                .limit(1)
                .fetchOne();
    }

    @Override
    public Topic queryTopicByDescription(Long descriptionId) {
        return queryFactory.select(topic)
                .from(topic, description)
                .where(description.topic.id.eq(topic.id))
                .where(description.id.eq(descriptionId))
                .fetchOne();
    }

    @Override
    public Topic queryTopicByChoice(Long choiceId) {
        return queryFactory.select(topic)
                .from(topic, choice)
                .where(choice.topic.id.eq(topic.id))
                .where(choice.id.eq(choiceId))
                .fetchOne();
    }

    @Override
    public List<Topic> queryTopicByChapterNum(Integer chapterNum) {
        return queryFactory.selectFrom(topic)
                .join(topic.category, category)
                .fetchJoin()
                .where(topic.chapter.number.eq(chapterNum))
                .fetch();
    }

    @Override
    public List<Tuple> queryAdminChapterDto(Integer chapterNum) {
        List<Tuple> result = queryFactory.select(topic.category.name, topic.title, topic.startDate, topic.endDate,
                        description.countDistinct() , choice.countDistinct(), keyword.countDistinct())
                .from(topic)
                .leftJoin(keyword).on(topic.id.eq(keyword.topic.id))
                .leftJoin(choice).on(topic.id.eq(choice.topic.id))
                .leftJoin(description).on(topic.id.eq(description.topic.id))
                .where(topic.chapter.number.eq(chapterNum))
                .groupBy(topic.id)
                .fetch();


        return result;
    }

    @Override
    public Map<String, Group> queryTopicAdminDto(String topicTitle) {
        Map<String, Group> transform = queryFactory.from(topic)
                .leftJoin(primaryDate).on(primaryDate.topic.eq(topic))
                .where(topic.title.eq(topicTitle))
                .transform(groupBy(topic.title).as(topic.chapter.number, topic.number, topic.category.name, topic.startDate,
                        topic.endDate, topic.detail, topic.startDateCheck, topic.endDateCheck, list(primaryDate)));
        return transform;
    }

    @Override
    public Map<String, Group> queryTopicCustomerDto(String topicTitle) {
         return queryFactory.from(topic)
                .leftJoin(primaryDate).on(primaryDate.topic.eq(topic))
                .leftJoin(sentence).on(sentence.topic.eq(topic))
                .leftJoin(keyword).on(keyword.topic.eq(topic))
                .where(topic.title.eq(topicTitle))
                .transform(groupBy(topic.title).as(topic.category.name, topic.startDate, topic.endDate,topic.chapter.number,
                        list(keyword), list(sentence.name), list(primaryDate)));
    }

    @Override
    public Map<Topic, List<PrimaryDate>> queryTimeFlowQuestion(Integer num) {
        Map<Topic, List<PrimaryDate>> transform = queryFactory.from(topic)
                .leftJoin(primaryDate).on(primaryDate.topic.eq(topic))
                .where(topic.chapter.number.eq(num))
                .transform(groupBy(topic).as(list(primaryDate)));
        return transform;
    }

    @Override
    public Map<Topic, List<PrimaryDate>> queryTimeFlowQuestion() {
        Map<Topic, List<PrimaryDate>> transform = queryFactory.from(topic)
                .leftJoin(primaryDate).on(primaryDate.topic.eq(topic))
                .transform(groupBy(topic).as(list(primaryDate)));
        return transform;
    }



    @Override
    public List<String> queryWrongTopicTitle(String topicTitle,int size) {
         return queryFactory.select(topic.title)
                 .from(topic)
                 .where(topic.title.ne(topicTitle))
                 .limit(size)
                 .fetch();
    }

    @Override
    public Optional<Topic> queryTopicByNumber(Integer chapterNum, Integer topicNum) {
        Topic findTopic = queryFactory.selectFrom(topic)
                .where(topic.chapter.number.eq(chapterNum))
                .where(topic.number.eq(topicNum))
                .fetchOne();
        return Optional.ofNullable(findTopic);
    }

    @Override
    public List<Tuple> queryTopicForTopicTempDto(int num) {
        return queryFactory.select(topic.title,topic.category.name, topic.startDate, topic.endDate )
                .from(topic)
                .where(topic.chapter.number.eq(num))
                .orderBy(topic.number.asc())
                .fetch();
    }

    @Override
    public List<Topic> queryTopicsByTopicTitleList(List<String> topicTitleList) {
        return queryFactory.selectFrom(topic)
                .where(topic.title.in(topicTitleList))
                .fetch();
    }
}
