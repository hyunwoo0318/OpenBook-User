package Project.OpenBook.Domain.Topic.TopicPrimaryDate.Repository;

import Project.OpenBook.Domain.Topic.Domain.QTopic;
import Project.OpenBook.Domain.Topic.TopicPrimaryDate.Domain.QTopicPrimaryDate;
import Project.OpenBook.Domain.Topic.TopicPrimaryDate.Domain.TopicPrimaryDate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static Project.OpenBook.Domain.Topic.Domain.QTopic.topic;
import static Project.OpenBook.Domain.Topic.TopicPrimaryDate.Domain.QTopicPrimaryDate.topicPrimaryDate;

@Repository
@RequiredArgsConstructor
public class TopicPrimaryDateRepositoryCustomImpl implements TopicPrimaryDateRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    @Override
    public List<TopicPrimaryDate> queryTopicPrimaryDateInChapter(Integer chapterNum) {
        if (chapterNum == -1) {
            return queryFactory.selectFrom(topicPrimaryDate).distinct()
                    .join(topicPrimaryDate.topic, topic).fetchJoin()
                    .leftJoin(topic.keywordList).fetchJoin()
                    .fetch();
        }

        return queryFactory.selectFrom(topicPrimaryDate).distinct()
                .join(topicPrimaryDate.topic, topic).fetchJoin()
                .leftJoin(topic.keywordList).fetchJoin()
                .where(topicPrimaryDate.topic.chapter.number.eq(chapterNum))
                .fetch();
    }
}
