package Project.OpenBook.Domain.Bookmark.Repository;

import static Project.OpenBook.Domain.Bookmark.Domain.QBookmark.bookmark;
import static Project.OpenBook.Domain.Category.Domain.QCategory.category;
import static Project.OpenBook.Domain.Chapter.Domain.QChapter.chapter;
import static Project.OpenBook.Domain.Era.QEra.era;
import static Project.OpenBook.Domain.QuestionCategory.Domain.QQuestionCategory.questionCategory;
import static Project.OpenBook.Domain.Topic.Domain.QTopic.topic;

import Project.OpenBook.Domain.Bookmark.Domain.Bookmark;
import Project.OpenBook.Domain.Bookmark.Domain.QBookmark;
import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class BookmarkRepositoryCustomImpl implements BookmarkRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Bookmark> queryBookmark(Long customerId, String topicTitle) {
        Bookmark bookmark = queryFactory.selectFrom(QBookmark.bookmark)
            .where(QBookmark.bookmark.customer.id.eq(customerId))
            .where(QBookmark.bookmark.topic.title.eq(topicTitle))
            .fetchOne();
        return Optional.ofNullable(bookmark);
    }

    @Override
    public List<Bookmark> queryBookmarks(Long customerId) {
        return queryFactory.selectFrom(bookmark).distinct()
            .leftJoin(bookmark.topic, topic).fetchJoin()
            .leftJoin(topic.chapter, chapter).fetchJoin()
            .leftJoin(topic.questionCategory, questionCategory).fetchJoin()
            .leftJoin(questionCategory.category, category).fetchJoin()
            .leftJoin(questionCategory.era, era).fetchJoin()
            .leftJoin(topic.keywordList).fetchJoin()
            .where(bookmark.customer.id.eq(customerId))
            .fetch();
    }

    @Override
    public List<Bookmark> queryBookmarks(Customer customer, List<Topic> topicList) {
        return queryFactory.selectFrom(bookmark)
            .where(bookmark.customer.eq(customer))
            .where(bookmark.topic.in(topicList))
            .fetch();
    }


}
