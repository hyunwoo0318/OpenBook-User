package Project.OpenBook.Domain.ChoiceComment.ChoiceKeyword;

import Project.OpenBook.Constants.CommentConst;
import Project.OpenBook.Domain.Choice.Domain.Choice;
import Project.OpenBook.Domain.ChoiceComment.Service.Dto.ChoiceCommentInfoDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static Project.OpenBook.Domain.Chapter.Domain.QChapter.chapter;
import static Project.OpenBook.Domain.Choice.Domain.QChoice.choice;
import static Project.OpenBook.Domain.ChoiceComment.QChoiceKeyword.choiceKeyword;
import static Project.OpenBook.Domain.Keyword.Domain.QKeyword.keyword;
import static Project.OpenBook.Domain.Topic.Domain.QTopic.topic;
import static com.querydsl.core.group.GroupBy.*;
import static com.querydsl.core.group.GroupBy.groupBy;

@Repository
@RequiredArgsConstructor
public class ChoiceKeywordRepositoryCustomImpl implements ChoiceKeywordRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Map<Choice, List<ChoiceCommentInfoDto>> queryChoiceKeywords(List<Choice> choiceList) {
        return queryFactory
                .from(choiceKeyword)
                .leftJoin(choiceKeyword.choice, choice)
                .leftJoin(choiceKeyword.keyword, keyword)
                .leftJoin(keyword.topic, topic)
                .leftJoin(topic.chapter, chapter)
                .where(choiceKeyword.choice.in(choiceList))
                .distinct()
                .transform(groupBy(choiceKeyword.choice)
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
