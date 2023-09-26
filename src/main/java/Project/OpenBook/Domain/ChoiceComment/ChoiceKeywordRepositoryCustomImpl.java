package Project.OpenBook.Domain.ChoiceComment;

import Project.OpenBook.Domain.Chapter.Domain.QChapter;
import Project.OpenBook.Domain.Choice.Domain.Choice;
import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import Project.OpenBook.Domain.Keyword.Domain.QKeyword;
import Project.OpenBook.Domain.Topic.Domain.QTopic;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static Project.OpenBook.Domain.Chapter.Domain.QChapter.chapter;
import static Project.OpenBook.Domain.ChoiceComment.QChoiceKeyword.choiceKeyword;
import static Project.OpenBook.Domain.Keyword.Domain.QKeyword.keyword;
import static Project.OpenBook.Domain.Topic.Domain.QTopic.topic;

@Repository
@RequiredArgsConstructor
public class ChoiceKeywordRepositoryCustomImpl implements ChoiceKeywordRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<ChoiceKeyword> queryChoiceKeywords(Choice inputChoice) {
        return queryFactory.selectFrom(choiceKeyword)
                .leftJoin(choiceKeyword.keyword, keyword).fetchJoin()
                .leftJoin(keyword.topic, topic).fetchJoin()
                .leftJoin(topic.chapter, chapter).fetchJoin()
                .where(choiceKeyword.choice.eq(inputChoice))
                .fetch();
    }
}
