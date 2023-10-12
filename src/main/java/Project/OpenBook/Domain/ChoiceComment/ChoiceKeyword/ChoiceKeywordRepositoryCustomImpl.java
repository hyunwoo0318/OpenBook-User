package Project.OpenBook.Domain.ChoiceComment.ChoiceKeyword;

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
import static Project.OpenBook.Domain.ChoiceComment.ChoiceKeyword.QChoiceKeyword.choiceKeyword;
import static Project.OpenBook.Domain.Description.Domain.QDescription.description;
import static Project.OpenBook.Domain.Description.Service.QDescriptionKeyword.descriptionKeyword;
import static Project.OpenBook.Domain.ExamQuestion.Domain.QExamQuestion.examQuestion;
import static Project.OpenBook.Domain.Keyword.Domain.QKeyword.keyword;
import static Project.OpenBook.Domain.Round.Domain.QRound.round;
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
                                choiceKeyword.keyword.name.as("name"),
                                choiceKeyword.keyword.id.as("id")
                        ))));
    }

    @Override
    public List<ChoiceKeyword> queryChoiceKeywords(String topicTitle) {
        return queryFactory.selectFrom(choiceKeyword)
                .leftJoin(choiceKeyword.keyword, keyword).fetchJoin()
                .leftJoin(choiceKeyword.choice, choice).fetchJoin()
                .leftJoin(choice.examQuestion, examQuestion).fetchJoin()
                .leftJoin(examQuestion.round, round).fetchJoin()
                .where(choiceKeyword.keyword.topic.title.eq(topicTitle))
                .fetch();
    }
}
