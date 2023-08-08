package Project.OpenBook.Repository.topic;

import Project.OpenBook.Domain.*;
import Project.OpenBook.Dto.PrimaryDate.PrimaryDateDto;
import Project.OpenBook.Dto.topic.TopicAdminDto;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static Project.OpenBook.Domain.QChoice.choice;
import static Project.OpenBook.Domain.QDescription.description;
import static Project.OpenBook.Domain.QKeyword.keyword;
import static Project.OpenBook.Domain.QPrimaryDate.primaryDate;
import static Project.OpenBook.Domain.QTopic.topic;


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
    public TopicAdminDto queryTopicAdminDto(String topicTitle) {
        Tuple r1 = queryFactory.select(topic.chapter.number, topic.number, topic.title, topic.category.name, topic.startDate,
                        topic.endDate, topic.detail, topic.startDateCheck, topic.endDateCheck)
                .from(topic)
                .where(topic.title.eq(topicTitle))
                .fetchOne();

        List<PrimaryDate> dateList = queryFactory.selectFrom(primaryDate)
                .where(primaryDate.topic.title.eq(topicTitle))
                .fetch();

        List<PrimaryDateDto> dateDtoList = dateList.stream().map(d -> new PrimaryDateDto(d.getExtraDate(), d.getExtraDateCheck(), d.getExtraDateComment()))
                .collect(Collectors.toList());

        Integer chapter = r1.get(topic.chapter.number);
        Integer number = r1.get(topic.number);
        String title = r1.get(topic.title);
        String category = r1.get(topic.category.name);
        Integer startDate = r1.get(topic.startDate);
        Integer endDate = r1.get(topic.endDate);
        String detail = r1.get(topic.detail);
        Boolean startDateCheck = r1.get(topic.startDateCheck);
        Boolean endDateCheck = r1.get(topic.endDateCheck);

        return new TopicAdminDto(chapter, title, category, startDate,startDateCheck, endDateCheck, endDate, detail, dateDtoList);
    }

    @Override
    public List<Tuple> queryTimeFlowQuestion(Integer num) {
        return queryFactory.select(topic, primaryDate)
                .from(topic)
                .leftJoin(primaryDate)
                .on(topic.id.eq(primaryDate.topic.id))
                .where(topic.chapter.number.eq(num))
                .fetch();
    }

    @Override
    public List<String> queryTopicTitleInChapter(Integer num) {
        return queryFactory.select(topic.title)
                .from(topic)
                .where(topic.chapter.number.eq(num))
                .fetch();
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
}
