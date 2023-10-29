package Project.OpenBook.Domain.Description.DescriptionKeyword;

import Project.OpenBook.Domain.Description.Domain.Description;
import Project.OpenBook.Domain.ExamQuestion.Service.dto.ExamQuestionCommentDto;
import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import com.querydsl.core.group.GroupBy;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static Project.OpenBook.Domain.Chapter.Domain.QChapter.chapter;
import static Project.OpenBook.Domain.Description.Domain.QDescription.description;
import static Project.OpenBook.Domain.Description.Service.QDescriptionKeyword.descriptionKeyword;
import static Project.OpenBook.Domain.ExamQuestion.Domain.QExamQuestion.examQuestion;
import static Project.OpenBook.Domain.Keyword.Domain.QKeyword.keyword;
import static Project.OpenBook.Domain.Round.Domain.QRound.round;
import static Project.OpenBook.Domain.Topic.Domain.QTopic.topic;
import static com.querydsl.core.group.GroupBy.groupBy;

@Repository
@RequiredArgsConstructor
public class DescriptionKeywordRepositoryCustomImpl implements DescriptionKeywordRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    @Override
    public List<DescriptionKeyword> queryDescriptionKeywordsAdmin(Description description) {
        return queryFactory.selectFrom(descriptionKeyword)
                .leftJoin(descriptionKeyword.keyword, keyword).fetchJoin()
                .leftJoin(keyword.topic, topic).fetchJoin()
                .leftJoin(topic.chapter, chapter).fetchJoin()
                .where(descriptionKeyword.description.eq(description))
                .fetch();
    }

    @Override
    public List<ExamQuestionCommentDto> queryDescriptionCustomer(Description description) {
//        return queryFactory.from(descriptionKeyword)
//                .leftJoin(descriptionKeyword.keyword, keyword)
//                .leftJoin(keyword.topic, topic)
//                .where(descriptionKeyword.description.eq(description))
//                .transform(groupBy(descriptionKeyword.description).as(list(Projections.constructor(
//                        ExamQuestionCommentDto.class,
//                        keyword.topic.dateComment.as("topicDateComment"),
//                        keyword.topic.title.as("topicTitle"),
//                        choiceKeyword.keyword.dateComment.as("keywordDateComment"),
//                        choiceKeyword.keyword.name.as("keywordName"),
//                        choiceKeyword.keyword.comment.as("keywordDateComment")
//                ))));
        return null;
    }

    @Override
    public Optional<Description> queryDescription(Integer roundNumber, Integer questionNumber) {
        Description findDescription = queryFactory.selectFrom(description)
                .leftJoin(description.descriptionKeywordList, descriptionKeyword).fetchJoin()
                .leftJoin(descriptionKeyword.keyword).fetchJoin()
                .leftJoin(keyword.topic, topic).fetchJoin()
                .leftJoin(topic.chapter, chapter).fetchJoin()
                .where(description.examQuestion.round.number.eq(roundNumber))
                .where(description.examQuestion.number.eq(questionNumber))
                .fetchOne();
        return Optional.ofNullable(findDescription);

    }

    @Override
    public List<DescriptionKeyword> queryDescriptionKeywordsAdmin(String topicTitle) {
        return queryFactory.selectFrom(descriptionKeyword)
                .leftJoin(descriptionKeyword.keyword,keyword).fetchJoin()
                .leftJoin(descriptionKeyword.description, description).fetchJoin()
                .leftJoin(description.examQuestion, examQuestion).fetchJoin()
                .leftJoin(examQuestion.round, round).fetchJoin()
                .where(descriptionKeyword.keyword.topic.title.eq(topicTitle))
                .fetch();


    }

    @Override
    public Map<Long, List<Keyword>> queryDescriptionKeywordsForInit() {
        return queryFactory.from(descriptionKeyword)
                .transform(groupBy(descriptionKeyword.description.examQuestion.id)
                        .as(GroupBy.list(
                                descriptionKeyword.keyword
                        )));
                
    }
}
