package Project.OpenBook.Repository.bookmark;


import Project.OpenBook.Domain.Bookmark;

import java.awt.print.Book;
import java.util.List;
import java.util.Optional;

public interface BookmarkRepositoryCustom {

    public Optional<Bookmark> queryBookmark(Long customerId, String topicTitle);

    public List<Bookmark> queryBookmarks(Long customerId);
}
