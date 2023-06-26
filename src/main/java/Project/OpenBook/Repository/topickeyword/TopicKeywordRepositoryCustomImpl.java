package Project.OpenBook.Repository.topickeyword;

import Project.OpenBook.Domain.TopicKeyword;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
