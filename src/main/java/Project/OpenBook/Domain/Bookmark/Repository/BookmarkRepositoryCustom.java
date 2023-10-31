package Project.OpenBook.Domain.Bookmark.Repository;


import Project.OpenBook.Domain.Bookmark.Domain.Bookmark;
import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.Topic.Domain.Topic;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepositoryCustom {

    public Optional<Bookmark> queryBookmark(Long customerId, String topicTitle);

    public List<Bookmark> queryBookmarks(Long customerId);

    public List<Bookmark> queryBookmarks(Customer customer, List<Topic> topicList);

}
