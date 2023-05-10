package Project.OpenBook.Service;

import Project.OpenBook.Domain.Bookmark;
import Project.OpenBook.Domain.Customer;
import Project.OpenBook.Domain.Topic;
import Project.OpenBook.Dto.BookmarkDto;
import Project.OpenBook.Repository.bookmark.BookmarkRepository;
import Project.OpenBook.Repository.CustomerRepository;
import Project.OpenBook.Repository.topic.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookmarkService {
    private final BookmarkRepository bookmarkRepository;
    private final CustomerRepository customerRepository;
    private final TopicRepository topicRepository;


    public Bookmark addBookmark(BookmarkDto bookmarkDto) {
        Long customerId = bookmarkDto.getCustomerId();
        String topicTitle = bookmarkDto.getTopicTitle();

        Optional<Customer> customerOptional = customerRepository.findById(customerId);
        if (customerOptional.isEmpty()) {
            return null;
        }
        Customer customer = customerOptional.get();

        Optional<Topic> topicOptional = topicRepository.findTopicByTitle(topicTitle);
        if (topicOptional.isEmpty()) {
            return null;
        }
        Topic topic = topicOptional.get();

        Bookmark bookmark = new Bookmark(customer, topic);
        bookmarkRepository.save(bookmark);
        return bookmark;
    }

    public boolean deleteBookmark(BookmarkDto bookmarkDto) {
        Long customerId = bookmarkDto.getCustomerId();
        String topicTitle = bookmarkDto.getTopicTitle();

        Optional<Customer> customerOptional = customerRepository.findById(customerId);
        if (customerOptional.isEmpty()) {
            return false;
        }

        Optional<Topic> topicOptional = topicRepository.findTopicByTitle(topicTitle);
        if (topicOptional.isEmpty()) {
            return false;
        }

        Bookmark bookmark = bookmarkRepository.queryBookmark(customerId, topicTitle);
        bookmarkRepository.delete(bookmark);
        return true;
    }

    public List<String> queryBookmarks(Long customerId) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);
        if (customerOptional.isEmpty()) {
            return null;
        }

        List<Bookmark> bookmarkList = bookmarkRepository.queryBookmarks(customerId);
        List<String> titleList = bookmarkList.stream().map(b -> b.getTopic().getTitle()).collect(Collectors.toList());
        return titleList;
    }
}
