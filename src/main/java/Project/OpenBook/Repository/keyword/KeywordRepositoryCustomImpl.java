package Project.OpenBook.Repository.keyword;

import Project.OpenBook.Domain.Keyword;
import Project.OpenBook.Domain.QKeyword;
import Project.OpenBook.Domain.QTopic;
import Project.OpenBook.Domain.QTopicKeyword;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static Project.OpenBook.Domain.QKeyword.keyword;
import static Project.OpenBook.Domain.QTopic.topic;
import static Project.OpenBook.Domain.QTopicKeyword.topicKeyword;

@Repository
@RequiredArgsConstructor
public class KeywordRepositoryCustomImpl implements KeywordRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public List<String> queryKeywordTopic(String keywordName) {
        return queryFactory.select(topicKeyword.topic.title)
                .from(topicKeyword)
                .where(keyword.name.eq(keywordName))
                .where(topicKeyword.topic.eq(topic))
                .where(topicKeyword.keyword.eq(keyword))
                .fetch();
    }

    @Override
    public List<Keyword> queryKeywordsList(List<String> keywordNameList) {
        return queryFactory.selectDistinct(keyword)
                .from(keyword)
                .where(keyword.name.in(keywordNameList))
                .fetch();
    }
}
