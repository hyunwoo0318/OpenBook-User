package Project.OpenBook.Repository.bookmark;

import Project.OpenBook.Domain.Bookmark;
import Project.OpenBook.Domain.QBookmark;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static Project.OpenBook.Domain.QBookmark.bookmark;

@Repository
@RequiredArgsConstructor
public class BookmarkRepositoryCustomImpl implements BookmarkRepositoryCustom{

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
        return queryFactory.selectFrom(bookmark)
                .where(bookmark.customer.id.eq(customerId))
                .fetch();
    }
}
