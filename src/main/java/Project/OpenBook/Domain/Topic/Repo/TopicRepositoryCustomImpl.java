package Project.OpenBook.Domain.Topic.Repo;

import Project.OpenBook.Domain.Chapter.Domain.QChapter;
import Project.OpenBook.Domain.Era.QEra;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import Project.OpenBook.Domain.Topic.Repo.TopicRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static Project.OpenBook.Domain.Category.Domain.QCategory.category;
import static Project.OpenBook.Domain.Chapter.Domain.QChapter.chapter;
import static Project.OpenBook.Domain.Era.QEra.era;
import static Project.OpenBook.Domain.Topic.Domain.QTopic.topic;


@Repository
@RequiredArgsConstructor
public class TopicRepositoryCustomImpl implements TopicRepositoryCustom {

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
    public Optional<Topic> queryTopicWithCategoryChapterEra(String topicTitle) {
        Topic findTopic = queryFactory.selectFrom(topic)
                .leftJoin(topic.category, category).fetchJoin()
                .leftJoin(topic.chapter, chapter).fetchJoin()
                .leftJoin(topic.era, era).fetchJoin()
                .where(topic.title.eq(topicTitle))
                .fetchOne();
        return Optional.ofNullable(findTopic);
    }

    @Override
    public Optional<Topic> queryTopicWithCategory(String topicTitle) {
        Topic findTopic = queryFactory.selectFrom(topic)
                .join(topic.category, category).fetchJoin()
                .where(topic.title.eq(topicTitle))
                .fetchOne();
        return Optional.ofNullable(findTopic);
    }

    @Override
    public List<Topic> queryTopicsWithChapter() {
        return queryFactory.selectFrom(topic)
                .leftJoin(topic.chapter, chapter).fetchJoin()
                .fetch();
    }
}
