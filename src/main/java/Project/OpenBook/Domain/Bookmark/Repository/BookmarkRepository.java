package Project.OpenBook.Domain.Bookmark.Repository;

import Project.OpenBook.Domain.Bookmark.Domain.Bookmark;
import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.Timeline.Domain.Timeline;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long>, BookmarkRepositoryCustom {

    public Optional<Bookmark> findByCustomerAndTopic(Customer customer, Topic topic);

    public Optional<Bookmark> findByCustomerAndTimeline(Customer customer, Timeline timeline);
}
