package Project.OpenBook.Domain.ChoiceComment;

import Project.OpenBook.Domain.Choice.Domain.Choice;
import Project.OpenBook.Domain.Sentence.Domain.Sentence;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static Project.OpenBook.Domain.Chapter.Domain.QChapter.chapter;
import static Project.OpenBook.Domain.ChoiceComment.QChoiceSentence.choiceSentence;
import static Project.OpenBook.Domain.Sentence.Domain.QSentence.sentence;
import static Project.OpenBook.Domain.Topic.Domain.QTopic.topic;

@Repository
@RequiredArgsConstructor
public class ChoiceSentenceRepositoryCustomImpl implements ChoiceSentenceRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<ChoiceSentence> queryChoiceSentences(Choice inputChoice) {
        return queryFactory.selectFrom(choiceSentence)
                .leftJoin(choiceSentence.sentence, sentence).fetchJoin()
                .leftJoin(sentence.topic, topic).fetchJoin()
                .leftJoin(topic.chapter, chapter).fetchJoin()
                .where(choiceSentence.choice.eq(inputChoice))
                .fetch();
    }
}
