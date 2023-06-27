package Project.OpenBook.Repository.topickeyword;

import Project.OpenBook.Domain.Topic;
import Project.OpenBook.Domain.TopicKeyword;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static Project.OpenBook.Domain.QTopicKeyword.topicKeyword;

@Service
@RequiredArgsConstructor
public class TopicKeywordRepositoryCustomImpl implements TopicKeywordRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    @Override
    public void deleteTopicKeyword(String topicTitle, String keywordName) {
        queryFactory.delete(topicKeyword)
                .where(topicKeyword.topic.title.eq(topicTitle))
                .where(topicKeyword.keyword.name.eq(keywordName))
                .execute();
    }

    @Override
    public TopicKeyword queryTopicKeyword(String topicTitle, String keywordName) {
        return queryFactory.selectFrom(topicKeyword)
                .where(topicKeyword.topic.title.eq(topicTitle))
                .where(topicKeyword.keyword.name.eq(keywordName))
                .fetchOne();
    }

    @Override
    public List<TopicKeyword> queryTopicKeyword(String topicTitle) {
        return queryFactory.selectFrom(topicKeyword)
                .where(topicKeyword.topic.title.eq(topicTitle))
                .fetch();
    }

    @Override
    public List<Topic> queryTopicsByKeyword(String keywordName) {
        return queryFactory.select(topicKeyword.topic)
                .from(topicKeyword)
                .where(topicKeyword.keyword.name.eq(keywordName))
                .fetch();
    }
}
