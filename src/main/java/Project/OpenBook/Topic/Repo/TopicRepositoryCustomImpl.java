package Project.OpenBook.Topic.Repo;

import Project.OpenBook.Chapter.Domain.QChapter;
import Project.OpenBook.Domain.*;
import Project.OpenBook.Topic.Domain.Topic;
import com.querydsl.core.Tuple;
import com.querydsl.core.group.Group;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static Project.OpenBook.Chapter.Domain.QChapter.chapter;
import static Project.OpenBook.Domain.QCategory.category;
import static Project.OpenBook.Domain.QKeyword.keyword;
import static Project.OpenBook.Domain.QPrimaryDate.primaryDate;
import static Project.OpenBook.Domain.QSentence.sentence;
import static Project.OpenBook.Topic.Domain.QTopic.topic;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;


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
