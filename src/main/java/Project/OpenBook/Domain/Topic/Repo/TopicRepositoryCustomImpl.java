package Project.OpenBook.Domain.Topic.Repo;

import static Project.OpenBook.Domain.Category.Domain.QCategory.category;
import static Project.OpenBook.Domain.Chapter.Domain.QChapter.chapter;
import static Project.OpenBook.Domain.Era.QEra.era;
import static Project.OpenBook.Domain.QuestionCategory.Domain.QQuestionCategory.questionCategory;
import static Project.OpenBook.Domain.Topic.Domain.QTopic.topic;

import Project.OpenBook.Domain.QuestionCategory.Domain.QuestionCategory;
import Project.OpenBook.Domain.Topic.Domain.Topic;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TopicRepositoryCustomImpl implements TopicRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    @Override
    public List<Topic> queryTopicsWithKeywordList(int chapterNum) {
        return queryFactory.selectFrom(topic).distinct()
            .leftJoin(topic.questionCategory, questionCategory).fetchJoin()
            .leftJoin(topic.keywordList).fetchJoin()
            .where(topic.chapter.number.eq(chapterNum))
            .fetch();
    }

    @Override
    public List<Topic> searchTopic(String input) {
        return queryFactory.selectFrom(topic).distinct()
            .leftJoin(topic.chapter, chapter).fetchJoin()
            .where(topic.title.contains(input))
            .fetch();
    }

    @Override
    public Optional<Topic> queryTopicWithCategory(String topicTitle) {
        Topic findTopic = queryFactory.selectFrom(topic)
            .leftJoin(topic.questionCategory, questionCategory).fetchJoin()
            .leftJoin(questionCategory.category, category).fetchJoin()
            .leftJoin(questionCategory.era, era).fetchJoin()
            .leftJoin(topic.topicPrimaryDateList).fetchJoin()
            .where(topic.title.eq(topicTitle))
            .fetchOne();
        return Optional.ofNullable(findTopic);
    }

    @Override
    public List<Topic> queryTopicsWithChapter() {
        return queryFactory.selectFrom(topic)
            .leftJoin(topic.chapter, chapter).fetchJoin()
            .fetch();
    }

    @Override
    public List<Topic> queryTopicsInQuestionCategory(Long questionCategoryId) {
        return queryFactory.selectFrom(topic)
            .leftJoin(topic.questionCategory, questionCategory).fetchJoin()
            .leftJoin(topic.chapter, chapter).fetchJoin()
            .leftJoin(questionCategory.category, category).fetchJoin()
            .where(topic.questionCategory.id.eq(questionCategoryId))
            .fetch();
    }

    @Override
    public List<Topic> queryTopicsWithCategory(int num) {
        return queryFactory.selectFrom(topic)
            .leftJoin(topic.questionCategory, questionCategory).fetchJoin()
            .leftJoin(questionCategory.category, category).fetchJoin()
            .leftJoin(questionCategory.era, era).fetchJoin()
            .leftJoin(topic.topicPrimaryDateList).fetchJoin()
            .where(topic.chapter.number.eq(num))
            .fetch();
    }

    @Override
    public List<Topic> queryTopicsInQuestionCategories(
        List<QuestionCategory> questionCategoryList) {
        return queryFactory.selectFrom(topic)
            .leftJoin(topic.questionCategory, questionCategory).fetchJoin()
            .where(topic.questionCategory.in(questionCategoryList))
            .fetch();
    }
}
