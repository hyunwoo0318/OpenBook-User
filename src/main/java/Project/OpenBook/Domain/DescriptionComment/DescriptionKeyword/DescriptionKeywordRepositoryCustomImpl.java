package Project.OpenBook.Domain.DescriptionComment.DescriptionKeyword;

import static Project.OpenBook.Domain.Chapter.Domain.QChapter.chapter;
import static Project.OpenBook.Domain.Description.Domain.QDescription.description;
import static Project.OpenBook.Domain.DescriptionComment.DescriptionKeyword.QDescriptionKeyword.descriptionKeyword;
import static Project.OpenBook.Domain.ExamQuestion.Domain.QExamQuestion.examQuestion;
import static Project.OpenBook.Domain.Keyword.Domain.QKeyword.keyword;
import static Project.OpenBook.Domain.Round.Domain.QRound.round;
import static Project.OpenBook.Domain.Topic.Domain.QTopic.topic;

import Project.OpenBook.Domain.Topic.Domain.Topic;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DescriptionKeywordRepositoryCustomImpl implements DescriptionKeywordRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<DescriptionKeyword> queryDescriptionKeywordsForInit() {
        return queryFactory.selectFrom(descriptionKeyword)
            .leftJoin(descriptionKeyword.keyword, keyword).fetchJoin()
            .fetch();
    }


    @Override
    public List<DescriptionKeyword> queryDescriptionKeywordForExamQuestion(Integer roundNumber) {
        return queryFactory.selectFrom(descriptionKeyword)
            .leftJoin(descriptionKeyword.description, description).fetchJoin()
            .leftJoin(descriptionKeyword.keyword, keyword).fetchJoin()
            .leftJoin(keyword.topic, topic).fetchJoin()
            .leftJoin(topic.chapter, chapter).fetchJoin()
            .where(description.examQuestion.round.number.eq(roundNumber))
            .fetch();
    }


    @Override
    public List<DescriptionKeyword> queryDescriptionKeywordsForTopicList(String topicTitle) {
        return queryFactory.selectFrom(descriptionKeyword)
            .leftJoin(descriptionKeyword.keyword, keyword).fetchJoin()
            .leftJoin(descriptionKeyword.description, description).fetchJoin()
            .leftJoin(description.examQuestion, examQuestion).fetchJoin()
            .leftJoin(examQuestion.round, round).fetchJoin()
            .where(descriptionKeyword.keyword.topic.title.eq(topicTitle))
            .fetch();
    }

    @Override
    public List<DescriptionKeyword> queryDescriptionKeywordsForTopicList(Integer chapterNum) {
        return queryFactory.selectFrom(descriptionKeyword)
            .leftJoin(descriptionKeyword.keyword, keyword).fetchJoin()
            .leftJoin(descriptionKeyword.description, description).fetchJoin()
            .leftJoin(description.examQuestion, examQuestion).fetchJoin()
            .leftJoin(examQuestion.round, round).fetchJoin()
            .where(keyword.topic.chapter.number.eq(chapterNum))
            .fetch();
    }

    @Override
    public List<DescriptionKeyword> queryDescriptionKeywordsForTopicList(List<Topic> topicList) {
        return queryFactory.selectFrom(descriptionKeyword)
            .leftJoin(descriptionKeyword.keyword, keyword).fetchJoin()
            .leftJoin(descriptionKeyword.description, description).fetchJoin()
            .leftJoin(description.examQuestion, examQuestion).fetchJoin()
            .leftJoin(examQuestion.round, round).fetchJoin()
            .where(keyword.topic.in(topicList))
            .fetch();
    }

    @Override
    public List<DescriptionKeyword> queryDescriptionKeywords(Long examQuestionId) {
        return queryFactory.selectFrom(descriptionKeyword)
            .leftJoin(descriptionKeyword.description, description).fetchJoin()
            .leftJoin(descriptionKeyword.keyword, keyword).fetchJoin()
            .leftJoin(keyword.topic, topic).fetchJoin()
            .where(description.examQuestion.id.eq(examQuestionId))
            .fetch();
    }
}
