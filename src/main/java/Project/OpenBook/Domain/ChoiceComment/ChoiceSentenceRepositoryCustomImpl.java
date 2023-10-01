package Project.OpenBook.Domain.ChoiceComment;

import Project.OpenBook.Constants.CommentConst;
import Project.OpenBook.Domain.Chapter.Domain.QChapter;
import Project.OpenBook.Domain.Choice.Domain.Choice;
import Project.OpenBook.Domain.Choice.Domain.QChoice;
import Project.OpenBook.Domain.Sentence.Domain.QSentence;
import Project.OpenBook.Domain.Sentence.Domain.Sentence;
import Project.OpenBook.Domain.Topic.Domain.QTopic;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static Project.OpenBook.Domain.Chapter.Domain.QChapter.chapter;
import static Project.OpenBook.Domain.Choice.Domain.QChoice.choice;
import static Project.OpenBook.Domain.ChoiceComment.QChoiceKeyword.choiceKeyword;
import static Project.OpenBook.Domain.ChoiceComment.QChoiceSentence.choiceSentence;
import static Project.OpenBook.Domain.ExamQuestion.Domain.QExamQuestion.examQuestion;
import static Project.OpenBook.Domain.Keyword.Domain.QKeyword.keyword;
import static Project.OpenBook.Domain.Sentence.Domain.QSentence.sentence;
import static Project.OpenBook.Domain.Topic.Domain.QTopic.topic;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;

@Repository
@RequiredArgsConstructor
public class ChoiceSentenceRepositoryCustomImpl implements ChoiceSentenceRepositoryCustom {
    private final JPAQueryFactory queryFactory;


    @Override
    public Map<Choice, List<ChoiceCommentInfoDto>> queryChoiceSentences(List<Choice> choiceList) {
        return queryFactory.from(choiceSentence)
                .leftJoin(choiceSentence.choice, choice)
                .leftJoin(choiceSentence.sentence, sentence)
                .leftJoin(sentence.topic, topic)
                .leftJoin(topic.chapter, chapter)
                .where(choiceSentence.choice.in(choiceList))
                .distinct()
                .transform(groupBy(choiceSentence.choice)
                        .as(list(Projections.constructor(
                                ChoiceCommentInfoDto.class,
                                topic.chapter.number.as("chapterNumber"),
                                sentence.topic.title.as("topicTitle"),
                                Expressions.as(Expressions.constant(CommentConst.SENTENCE), "type"),
                                choiceSentence.sentence.name.as("name"),
                                choiceSentence.sentence.id.as("id")
                        ))));
    }
}
