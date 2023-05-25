package Project.OpenBook.Service;

import Project.OpenBook.Constants.ErrorCode;
import Project.OpenBook.CustomException;
import Project.OpenBook.Domain.Bookmark;
import Project.OpenBook.Domain.Customer;
import Project.OpenBook.Domain.Question;
import Project.OpenBook.Domain.Topic;
import Project.OpenBook.Dto.BookmarkDto;
import Project.OpenBook.Repository.bookmark.BookmarkRepository;
import Project.OpenBook.Repository.customer.CustomerRepository;
import Project.OpenBook.Repository.topic.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static Project.OpenBook.Constants.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class BookmarkService {
    private final BookmarkRepository bookmarkRepository;
    private final CustomerRepository customerRepository;
    private final TopicRepository topicRepository;


    public Bookmark addBookmark(BookmarkDto bookmarkDto) {
        Long customerId = bookmarkDto.getCustomerId();
        String topicTitle = bookmarkDto.getTopicTitle();

        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> {
            throw new CustomException(CUSTOMER_NOT_FOUND);
        });

        Topic topic = topicRepository.findTopicByTitle(topicTitle).orElseThrow(() -> {
            throw new CustomException(TOPIC_NOT_FOUND);
        });

        Bookmark bookmark = new Bookmark(customer, topic);
        bookmarkRepository.save(bookmark);
        return bookmark;
    }

    public void deleteBookmark(BookmarkDto bookmarkDto) {
        Long customerId = bookmarkDto.getCustomerId();
        String topicTitle = bookmarkDto.getTopicTitle();

        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> {
            throw new CustomException(CUSTOMER_NOT_FOUND);
        });

        Topic topic = topicRepository.findTopicByTitle(topicTitle).orElseThrow(() -> {
            throw new CustomException(TOPIC_NOT_FOUND);
        });

        Bookmark bookmark = bookmarkRepository.queryBookmark(customerId, topicTitle);
        bookmarkRepository.delete(bookmark);
    }

    public List<String> queryBookmarks(Long customerId) {
        customerRepository.findById(customerId).orElseThrow(() -> {
            throw new CustomException(CUSTOMER_NOT_FOUND);
        });

        List<Bookmark> bookmarkList = bookmarkRepository.queryBookmarks(customerId);
        List<String> titleList = bookmarkList.stream().map(b -> b.getTopic().getTitle()).collect(Collectors.toList());
        return titleList;
    }
}
