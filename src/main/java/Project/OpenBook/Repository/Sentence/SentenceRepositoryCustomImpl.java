package Project.OpenBook.Repository.Sentence;

import Project.OpenBook.Domain.QSentence;
import Project.OpenBook.Domain.Sentence;
import com.querydsl.jpa.impl.JPAQuery;
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
}
