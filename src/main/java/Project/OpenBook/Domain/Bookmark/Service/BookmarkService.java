package Project.OpenBook.Domain.Bookmark.Service;

import Project.OpenBook.Domain.Bookmark.Domain.Bookmark;
import Project.OpenBook.Domain.Bookmark.Repository.BookmarkRepository;
import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import Project.OpenBook.Domain.Topic.Repo.TopicRepository;
import Project.OpenBook.Handler.Exception.CustomException;
import Project.OpenBook.Domain.Customer.Repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static Project.OpenBook.Constants.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class BookmarkService {
    private final BookmarkRepository bookmarkRepository;
    private final CustomerRepository customerRepository;
    private final TopicRepository topicRepository;


    public Bookmark addBookmark(Customer customer, String topicTitle) {

        Topic topic = topicRepository.findTopicByTitle(topicTitle).orElseThrow(() -> {
            throw new CustomException(TOPIC_NOT_FOUND);
        });

        Bookmark bookmark = new Bookmark(customer, topic);
        bookmarkRepository.save(bookmark);
        return bookmark;
    }

    public void deleteBookmark(Customer customer, String topicTitle) {
        Topic topic = topicRepository.findTopicByTitle(topicTitle).orElseThrow(() -> {
            throw new CustomException(TOPIC_NOT_FOUND);
        });

        bookmarkRepository.queryBookmark(customer.getId(), topicTitle)
                .ifPresent(bookmarkRepository::delete);
    }

    public List<String> queryBookmarks(Customer customer) {
        List<Bookmark> bookmarkList = bookmarkRepository.queryBookmarks(customer.getId());
        List<String> titleList = bookmarkList.stream().map(b -> b.getTopic().getTitle()).collect(Collectors.toList());
        return titleList;
    }
}
