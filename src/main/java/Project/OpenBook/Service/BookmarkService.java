package Project.OpenBook.Service;

import Project.OpenBook.Utils.CustomException;
import Project.OpenBook.Domain.Bookmark;
import Project.OpenBook.Domain.Customer;
import Project.OpenBook.Topic.Domain.Topic;
import Project.OpenBook.Repository.bookmark.BookmarkRepository;
import Project.OpenBook.Repository.customer.CustomerRepository;
import Project.OpenBook.Topic.Repo.TopicRepository;
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
