package Project.OpenBook.Domain.Sentence.Repository;

import Project.OpenBook.Domain.Sentence.Domain.QSentence;
import Project.OpenBook.Domain.Sentence.Domain.Sentence;
import Project.OpenBook.Domain.Topic.Domain.QTopic;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static Project.OpenBook.Domain.Sentence.Domain.QSentence.*;
import static Project.OpenBook.Domain.Topic.Domain.QTopic.topic;


@Repository
@RequiredArgsConstructor
public class SentenceRepositoryCustomImpl implements SentenceRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Sentence> queryByTopicTitle(String topicTitle, int limit) {
        return queryFactory.selectFrom(sentence)
                .where(sentence.topic.title.eq(topicTitle))
                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
                .limit(limit)
                .fetch();
    }

    @Override
    public List<Sentence> queryWrongSentences(String answerTopicTitle, int limit) {
        return queryFactory.selectFrom(sentence)
                .join(sentence.topic, topic).fetchJoin()
                .where(sentence.topic.title.ne(answerTopicTitle))
                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
                .limit(limit)
                .fetch();
    }
}
