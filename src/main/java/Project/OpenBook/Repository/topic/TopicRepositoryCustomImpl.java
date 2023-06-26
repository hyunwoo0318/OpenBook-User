package Project.OpenBook.Repository.topic;

import Project.OpenBook.Domain.QChoice;
import Project.OpenBook.Domain.QDescription;
import Project.OpenBook.Domain.Topic;
import Project.OpenBook.Dto.topic.AdminChapterDto;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.MathExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static Project.OpenBook.Domain.QChoice.choice;
import static Project.OpenBook.Domain.QDescription.description;
import static Project.OpenBook.Domain.QKeyword.keyword;
import static Project.OpenBook.Domain.QTopic.topic;
import static Project.OpenBook.Domain.QTopicKeyword.topicKeyword;

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
    public List<String> queryTopicKeywords(String topicTitle) {
        return queryFactory.select(topicKeyword.keyword.name)
                .from(topicKeyword)
                .where(topic.title.eq(topicTitle))
                .where(topicKeyword.topic.eq(topic))
                .where(topicKeyword.keyword.eq(keyword))
                .fetch();
    }


    @Override
    public List<AdminChapterDto> queryAdminChapterDto(Integer chapterNum) {
        List<Tuple> result = queryFactory.select(topic.category.name, topic.title, topic.startDate, topic.endDate,
                        description.count(), choice.count(), topicKeyword.count())
                .from(topic)
                .leftJoin(topicKeyword).on(topic.id.eq(topicKeyword.topic.id))
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
            Long descriptionCount = t.get(description.count());
            if(descriptionCount == null) descriptionCount = 0L;
            Long choiceCount = t.get(choice.count());
            if(choiceCount == null) choiceCount = 0L;
            Long keywordCount = t.get(keyword.count());
            if(keywordCount == null) keywordCount = 0L;
            AdminChapterDto adminChapterDto = new AdminChapterDto(category, title, startDate, endDate, descriptionCount, choiceCount, keywordCount);
            adminChapterDtoList.add(adminChapterDto);
        }
        return adminChapterDtoList;
    }

}
