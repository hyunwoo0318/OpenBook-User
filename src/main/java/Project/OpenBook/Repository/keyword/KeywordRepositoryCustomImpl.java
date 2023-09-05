package Project.OpenBook.Repository.keyword;

import Project.OpenBook.Domain.Keyword;

import Project.OpenBook.Domain.QTopic;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

import static Project.OpenBook.Domain.QKeyword.keyword;
import static Project.OpenBook.Domain.QTopic.topic;

@Repository
@RequiredArgsConstructor
public class KeywordRepositoryCustomImpl implements KeywordRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Keyword> queryByNameInTopic(String keywordName, String topicTitle) {
        Keyword findKeyword = queryFactory.selectFrom(keyword)
                .where(keyword.name.eq(keywordName))
                .where(keyword.topic.title.eq(topicTitle))
                .fetchOne();
        return Optional.ofNullable(findKeyword);
    }

    @Override
    public List<Keyword> queryKeywordsInTopic(String topicTitle) {
        return queryFactory.selectFrom(keyword)
                .where(keyword.topic.title.eq(topicTitle))
                .fetch();
    }

    @Override
    public List<Keyword> queryKeywordsInTopicWithLimit(String topicTitle, int limit) {
        return queryFactory.selectFrom(keyword)
                .where(keyword.topic.title.eq(topicTitle))
                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
                .limit(limit)
                .fetch();
    }

//    @Override
//    public List<Keyword> queryKeywordsList(List<String> keywordNameList) {
//        return queryFactory.selectDistinct(keyword)
//                .from(keyword)
//                .where(keyword.name.in(keywordNameList))
//                .fetch();
//    }

    @Override
    public List<Tuple> queryWrongKeywords(String answerTopicTitle, int limit) {
        return queryFactory.select(keyword.name, keyword.comment,keyword.topic.title)
                .from(keyword)
                .where(keyword.topic.title.ne(answerTopicTitle))
                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
                .limit(limit)
                .fetch();
    }
}
