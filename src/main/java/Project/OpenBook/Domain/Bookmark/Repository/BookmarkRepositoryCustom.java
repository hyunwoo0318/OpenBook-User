package Project.OpenBook.Domain.Bookmark.Repository;


import Project.OpenBook.Domain.Bookmark.Domain.Bookmark;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepositoryCustom {

    public Optional<Bookmark> queryBookmark(Long customerId, String topicTitle);

    public List<Bookmark> queryBookmarks(Long customerId);
}
