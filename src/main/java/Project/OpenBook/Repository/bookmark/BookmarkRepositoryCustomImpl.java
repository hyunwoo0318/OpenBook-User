package Project.OpenBook.Repository.bookmark;

import Project.OpenBook.Domain.Bookmark;
import Project.OpenBook.Domain.QBookmark;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static Project.OpenBook.Domain.QBookmark.bookmark;

@Repository
@RequiredArgsConstructor
public class BookmarkRepositoryCustomImpl implements BookmarkRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Bookmark queryBookmark(Long customerId, String topicTitle) {
        return queryFactory.selectFrom(bookmark)
                .where(bookmark.customer.id.eq(customerId))
                .where(bookmark.topic.title.eq(topicTitle))
                .fetchOne();
    }

    @Override
    public List<Bookmark> queryBookmarks(Long customerId) {
        return queryFactory.selectFrom(bookmark)
                .where(bookmark.customer.id.eq(customerId))
                .fetch();
    }
}
