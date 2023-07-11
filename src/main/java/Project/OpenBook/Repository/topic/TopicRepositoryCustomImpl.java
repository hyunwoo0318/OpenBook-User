package Project.OpenBook.Repository.topic;

import Project.OpenBook.Domain.*;
import Project.OpenBook.Dto.chapter.ChapterTitleDto;
import Project.OpenBook.Dto.topic.AdminChapterDto;
import Project.OpenBook.Dto.topic.TopicDto;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.MathExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static Project.OpenBook.Domain.QChoice.choice;
import static Project.OpenBook.Domain.QDescription.description;
import static Project.OpenBook.Domain.QKeyword.keyword;
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
    public List<Keyword> queryTopicKeywords(String topicTitle) {
        return null;
    }


    @Override
    public List<AdminChapterDto> queryAdminChapterDto(Integer chapterNum) {
        List<Tuple> result = queryFactory.select(topic.category.name, topic.title, topic.startDate, topic.endDate,
                        description.countDistinct(), choice.countDistinct(), keyword.countDistinct())
                .from(topic)
                .leftJoin(keyword).on(topic.id.eq(keyword.topic.id))
                .leftJoin(choice).on(topic.id.eq(choice.topic.id))
                .leftJoin(description).on(topic.id.eq(description.topic.id))
                .where(topic.chapter.number.eq(chapterNum))
                .groupBy(topic.id)
                .fetch();

        List<AdminChapterDto> adminChapterDtoList = new ArrayList<>();
        for (Tuple t : result) {
            String category = t.get(topic.category.name);
            String title = t.get(topic.title);
            Integer startDate = t.get(topic.startDate);
            Integer endDate = t.get(topic.endDate);
            Long descriptionCount = t.get(description.countDistinct());
            if(descriptionCount == null) descriptionCount = 0L;
            Long choiceCount = t.get(choice.countDistinct());
            if(choiceCount == null) choiceCount = 0L;
            Long keywordCount = t.get(keyword.countDistinct());
            if(keywordCount == null) keywordCount = 0L;
            AdminChapterDto adminChapterDto = new AdminChapterDto(category, title, startDate, endDate, descriptionCount, choiceCount, keywordCount);
            adminChapterDtoList.add(adminChapterDto);
        }
        return adminChapterDtoList;
    }

    @Override
    public TopicDto queryTopicDto(String topicTitle) {
        Tuple r = queryFactory.select(topic.chapter.number, topic.title, topic.category.name, topic.startDate,
                        topic.endDate, topic.detail)
                .from(topic)
                .where(topic.title.eq(topicTitle))
                .fetchOne();


            Integer chapter = r.get(topic.chapter.number);
            String title = r.get(topic.title);
            String category = r.get(topic.category.name);
            Integer startDate = r.get(topic.startDate);
            Integer endDate = r.get(topic.endDate);
            String detail = r.get(topic.detail);

        return new TopicDto(chapter, title, category, startDate, endDate, detail);

    }

}
