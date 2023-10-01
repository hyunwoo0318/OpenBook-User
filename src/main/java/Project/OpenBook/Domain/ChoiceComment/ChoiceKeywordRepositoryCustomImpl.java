package Project.OpenBook.Domain.ChoiceComment;

import Project.OpenBook.Constants.ChoiceType;
import Project.OpenBook.Constants.CommentConst;
import Project.OpenBook.Domain.Choice.Domain.Choice;
import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import Project.OpenBook.Domain.Topic.Domain.QTopic;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static Project.OpenBook.Domain.Chapter.Domain.QChapter.chapter;
import static Project.OpenBook.Domain.Choice.Domain.QChoice.choice;
import static Project.OpenBook.Domain.ChoiceComment.QChoiceKeyword.choiceKeyword;
import static Project.OpenBook.Domain.ExamQuestion.Domain.QExamQuestion.examQuestion;
import static Project.OpenBook.Domain.Keyword.Domain.QKeyword.keyword;
import static Project.OpenBook.Domain.Topic.Domain.QTopic.topic;
import static com.querydsl.core.group.GroupBy.*;
import static com.querydsl.core.group.GroupBy.groupBy;

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

    @Override
    public Map<Choice, List<ChoiceCommentInfoDto>>  queryChoiceKeywordsTemp(Integer roundNumber, Integer questionNumber) {
        return queryFactory
                .from(choiceKeyword)
                .leftJoin(choiceKeyword.choice, choice)
                .leftJoin(choiceKeyword.keyword, keyword)
                .leftJoin(keyword.topic, topic)
                .leftJoin(topic.chapter, chapter)
                .where(choiceKeyword.choice.in(
                        JPAExpressions.select(choice)
                                .from(examQuestion)
                                .where(choice.examQuestion.number.eq(questionNumber)
                                        .and(choice.examQuestion.round.number.eq(roundNumber)))
                ))
                .distinct()
                .transform(groupBy(choiceKeyword.choice)
//                .as(list(choiceKeyword.keyword)));
                        .as(list(Projections.constructor(
                                ChoiceCommentInfoDto.class,
                                topic.chapter.number.as("chapterNumber"),
                                keyword.topic.title.as("topicTitle"),
                                Expressions.as(Expressions.constant(CommentConst.KEYWORD), "type"),
                                choiceKeyword.keyword.name.as("name"),
                                choiceKeyword.keyword.id.as("id")
                        ))));

    }
}
