package Project.OpenBook.Repository.sentence;

import Project.OpenBook.Domain.Sentence;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static Project.OpenBook.Domain.QSentence.sentence;

@Repository
@RequiredArgsConstructor
public class SentenceRepositoryCustomImpl implements SentenceRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    @Override
    public List<Sentence> queryByTopicTitle(String topicTitle) {
        return queryFactory.selectFrom(sentence)
                .where(sentence.topic.title.eq(topicTitle))
                .fetch();
    }

    @Override
    public List<Sentence> queryByTopicTitle(String topicTitle, int size) {
        return queryFactory.selectFrom(sentence)
                .where(sentence.topic.title.eq(topicTitle))
                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
                .limit(size)
                .fetch();
    }

    @Override
    public List<Tuple> queryWrongSentences(String answerTopicTitle, int size) {
        return queryFactory.select(sentence.name,sentence.topic.title)
                .from(sentence)
                .where(sentence.topic.title.ne(answerTopicTitle))
                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
                .limit(size)
                .fetch();
    }
}
