package Project.OpenBook.Repository.bookmark;


import Project.OpenBook.Domain.Bookmark;

import java.awt.print.Book;
import java.util.List;

public interface BookmarkRepositoryCustom {

    public Bookmark queryBookmark(Long customerId, String topicTitle);

    public List<Bookmark> queryBookmarks(Long customerId);
}
