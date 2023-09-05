package Project.OpenBook.Repository.sentence;

import Project.OpenBook.Domain.QSentence;
import Project.OpenBook.Domain.Sentence;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static Project.OpenBook.Domain.QSentence.sentence;

@Repository
@RequiredArgsConstructor
public class SentenceRepositoryCustomImpl implements SentenceRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Sentence> querySentenceByContentInTopic(String name, String topicTitle) {
        Sentence sentence = queryFactory.selectFrom(QSentence.sentence)
                .where(QSentence.sentence.topic.title.eq(topicTitle))
                .where(QSentence.sentence.name.eq(name))
                .fetchOne();
        return Optional.ofNullable(sentence);
    }

    @Override
    public List<Sentence> queryByTopicTitle(String topicTitle) {
        return queryFactory.selectFrom(sentence)
                .where(sentence.topic.title.eq(topicTitle))
                .fetch();
    }

    @Override
    public List<Sentence> queryByTopicTitle(String topicTitle, int limit) {
        return queryFactory.selectFrom(sentence)
                .where(sentence.topic.title.eq(topicTitle))
                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
                .limit(limit)
                .fetch();
    }

    @Override
    public List<Tuple> queryWrongSentences(String answerTopicTitle, int limit) {
        return queryFactory.select(sentence.name,sentence.topic.title)
                .from(sentence)
                .where(sentence.topic.title.ne(answerTopicTitle))
                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
                .limit(limit)
                .fetch();
    }
}
