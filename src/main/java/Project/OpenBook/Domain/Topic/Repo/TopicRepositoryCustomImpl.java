package Project.OpenBook.Domain.Topic.Repo;

import Project.OpenBook.Domain.Topic.Domain.Topic;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static Project.OpenBook.Domain.Category.Domain.QCategory.category;
import static Project.OpenBook.Domain.Chapter.Domain.QChapter.chapter;
import static Project.OpenBook.Domain.Topic.Domain.QTopic.topic;


@Repository
@RequiredArgsConstructor
public class TopicRepositoryCustomImpl implements TopicRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<String> queryWrongTopicTitle(String topicTitle,int size) {
         return queryFactory.select(topic.title)
                 .from(topic)
                 .where(topic.title.ne(topicTitle))
                 .limit(size)
                 .fetch();
    }

    @Override
    public List<Topic> queryTopicsByTopicTitleList(List<String> topicTitleList) {
        return queryFactory.selectFrom(topic)
                .where(topic.title.in(topicTitleList))
                .fetch();
    }

    @Override
    public Optional<Topic> queryTopicWithCategoryChapter(String topicTitle) {
        Topic findTopic = queryFactory.selectFrom(topic)
                .join(topic.category, category)
                .join(topic.chapter, chapter)
                .where(topic.title.eq(topicTitle))
                .fetchOne();
        return Optional.ofNullable(findTopic);
    }

    @Override
    public Optional<Topic> queryTopicWithCategory(String topicTitle) {
        Topic findTopic = queryFactory.selectFrom(topic)
                .join(topic.category, category)
                .where(topic.title.eq(topicTitle))
                .fetchOne();
        return Optional.ofNullable(findTopic);
    }
}
