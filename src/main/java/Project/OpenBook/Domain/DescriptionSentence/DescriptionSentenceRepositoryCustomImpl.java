package Project.OpenBook.Domain.DescriptionSentence;

import Project.OpenBook.Domain.Description.Domain.Description;
import Project.OpenBook.Domain.Description.Domain.QDescription;
import Project.OpenBook.Domain.Sentence.Domain.QSentence;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static Project.OpenBook.Domain.Chapter.Domain.QChapter.chapter;
import static Project.OpenBook.Domain.Description.Service.QDescriptionKeyword.descriptionKeyword;
import static Project.OpenBook.Domain.DescriptionSentence.QDescriptionSentence.descriptionSentence;
import static Project.OpenBook.Domain.Keyword.Domain.QKeyword.keyword;
import static Project.OpenBook.Domain.Sentence.Domain.QSentence.sentence;
import static Project.OpenBook.Domain.Topic.Domain.QTopic.topic;

@Repository
@RequiredArgsConstructor
public class DescriptionSentenceRepositoryCustomImpl implements DescriptionSentenceRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<DescriptionSentence> queryDescriptionSentences(Description description) {

        return queryFactory.selectFrom(descriptionSentence)
                .leftJoin(descriptionSentence.sentence, sentence).fetchJoin()
                .leftJoin(sentence.topic, topic).fetchJoin()
                .leftJoin(topic.chapter, chapter).fetchJoin()
                .where(descriptionSentence.description.eq(description))
                .fetch();
    }
}
