package Project.OpenBook.Domain.Keyword.Repository;

import Project.OpenBook.Domain.Chapter.Domain.Chapter;
import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import Project.OpenBook.Domain.QuestionCategory.Domain.QuestionCategory;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static Project.OpenBook.Domain.Chapter.Domain.QChapter.chapter;
import static Project.OpenBook.Domain.Keyword.Domain.QKeyword.keyword;
import static Project.OpenBook.Domain.QuestionCategory.Domain.QQuestionCategory.questionCategory;
import static Project.OpenBook.Domain.Topic.Domain.QTopic.topic;


@Repository
@RequiredArgsConstructor
public class KeywordRepositoryCustomImpl implements KeywordRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Keyword> queryByNameInTopic(String keywordName, String topicTitle) {
        Keyword findKeyword = queryFactory.selectFrom(keyword)
                .where(keyword.name.eq(keywordName))
                .where(keyword.topic.title.eq(topicTitle))
                .fetchOne();
        return Optional.ofNullable(findKeyword);
    }

    @Override
    public List<Keyword> queryKeywordsInTopicWithLimit(String topicTitle, int limit) {
        return queryFactory.selectFrom(keyword)
                .where(keyword.topic.title.eq(topicTitle))
                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
                .limit(limit)
                .fetch();
    }



    @Override
    public List<Keyword> queryWrongKeywords(String answerTopicTitle, int limit) {
        return queryFactory.selectFrom(keyword)
                .leftJoin(keyword.topic, topic).fetchJoin()
                .where(
                        keyword.topic.questionCategory.eq(
                                JPAExpressions.select(topic.questionCategory)
                                        .from(topic)
                                        .where(topic.title.eq(answerTopicTitle))
                        )
                )
                .where(keyword.topic.title.ne(answerTopicTitle))
                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
                .limit(limit)
                .fetch();
    }

    @Override
    public List<Keyword> queryWrongKeywords(List<String> keywordNameList, String answerTopicTitle) {
        return queryFactory.selectFrom(keyword)
                .leftJoin(keyword.topic, topic).fetchJoin()
                .where(
                        keyword.topic.questionCategory.eq(
                                JPAExpressions.select(topic.questionCategory)
                                        .from(topic)
                                        .where(topic.title.eq(answerTopicTitle))
                        )
                )
                .where(keyword.topic.title.ne(answerTopicTitle))
                .where(keyword.name.notIn(keywordNameList))
                .fetch();
    }

    @Override
    public List<Keyword> queryKeywordsInTopic(String topicTitle) {
        return queryFactory.selectFrom(keyword)
                .leftJoin(keyword.topic, topic).fetchJoin()
                .leftJoin(topic.chapter, chapter).fetchJoin()
                .where(keyword.topic.title.eq(topicTitle))
                .fetch();
    }

    @Override
    public List<Keyword> queryKeywordsInTopicWithPrimaryDate(String topicTitle) {
        return queryFactory.select(keyword).distinct()
                .from(keyword)
                .leftJoin(keyword.topic, topic).fetchJoin()
                .leftJoin(keyword.keywordPrimaryDateList).fetchJoin()
                .where(keyword.topic.title.eq(topicTitle))
                .fetch();
    }

    @Override
    public List<Keyword> queryKeywordsInTopicWithPrimaryDate(Integer chapterNum) {
        return queryFactory.select(keyword).distinct()
                .from(keyword)
                .leftJoin(keyword.topic, topic).fetchJoin()
                .leftJoin(keyword.keywordPrimaryDateList).fetchJoin()
                .where(keyword.topic.chapter.number.eq(chapterNum))
                .fetch();
    }

    @Override
    public List<Keyword> queryKeywordsInTopicWithPrimaryDate(List<Topic> topicList) {
        return queryFactory.select(keyword).distinct()
                .from(keyword)
                .leftJoin(keyword.topic, topic).fetchJoin()
                .leftJoin(keyword.keywordPrimaryDateList).fetchJoin()
                .where(keyword.topic.in(topicList))
                .fetch();
    }

    @Override
    public List<Keyword> queryKeywordsWithChapter() {
        return queryFactory.selectFrom(keyword)
                .leftJoin(keyword.topic,topic).fetchJoin()
                .leftJoin(topic.chapter, chapter).fetchJoin()
                .fetch();
    }

    @Override
    public List<Keyword> queryKeywordsForUpdateHistory(List<Long> keywordIdList) {
        return queryFactory.selectFrom(keyword)
                .leftJoin(keyword.topic, topic).fetchJoin()
                .leftJoin(topic.questionCategory, questionCategory).fetchJoin()
                .where(keyword.id.in(keywordIdList))
                .fetch();
    }

    @Override
    public List<Keyword> queryKeywordsInQuestionCategory(QuestionCategory questionCategory) {
        return queryFactory.selectFrom(keyword)
                .leftJoin(keyword.topic, topic).fetchJoin()
                .where(keyword.topic.questionCategory.eq(questionCategory))
                .fetch();
    }

    @Override
    public List<Keyword> queryKeywordsWithTopic() {
        return queryFactory.selectFrom(keyword)
                .leftJoin(keyword.topic,topic).fetchJoin()
                .fetch();
    }

    @Override
    public List<Keyword> queryKeywordsForInit() {
        return queryFactory.selectFrom(keyword)
                .leftJoin(keyword.topic,topic).fetchJoin()
                .leftJoin(topic.questionCategory, questionCategory).fetchJoin()
                .fetch();
    }

    @Override
    public List<Keyword> queryRandomOpenedKeywords(Topic answerTopic, Integer count) {
        Chapter answerChapter = answerTopic.getChapter();
        return queryFactory.selectFrom(keyword)
                .leftJoin(keyword.topic, topic).fetchJoin()
                .leftJoin(topic.questionCategory, questionCategory).fetchJoin()
                .where(topic.chapter.number.lt(answerChapter.getNumber())
                        .or(topic.chapter.number.eq(answerChapter.getNumber())
                                .and(topic.number.lt(answerTopic.getNumber()))
                        )
                )
                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
                .limit(count)
                .fetch();
    }

    @Override
    public List<Keyword> queryKeywordsInQuestionCategories(List<Keyword> keywordList) {
        List<QuestionCategory> questionCategoryList = new ArrayList<>();
        List<String> keywordNameList = new ArrayList<>();
        List<Topic> keywordTopicList = new ArrayList<>();

        for (Keyword k : keywordList) {
            Topic t = k.getTopic();
            questionCategoryList.add(t.getQuestionCategory());
            keywordNameList.add(t.getTitle());
            keywordTopicList.add(t);
        }
        return queryFactory.selectFrom(keyword)
                .leftJoin(keyword.topic, topic).fetchJoin()
                .leftJoin(topic.questionCategory, questionCategory).fetchJoin()
                .where(keyword.topic.questionCategory.in(questionCategoryList))
                .where(keyword.topic.notIn(keywordTopicList))
                .where(keyword.name.notIn(keywordNameList))
                .fetch();
    }

    //    @Override
//    public List<Keyword> queryTotalAnswerKeywordsInJJH(String pivTopicTitle) {
//        return queryFactory.selectFrom(keyword)
//                .leftJoin(keyword.topic,topic).fetchJoin()
//                .leftJoin(topic.questionCategory, questionCategory).fetchJoin()
//    }
}
