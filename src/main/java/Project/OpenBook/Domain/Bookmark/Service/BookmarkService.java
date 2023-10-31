package Project.OpenBook.Domain.Bookmark.Service;

import Project.OpenBook.Domain.Bookmark.Domain.Bookmark;
import Project.OpenBook.Domain.Bookmark.Dto.BookmarkDto;
import Project.OpenBook.Domain.Bookmark.Repository.BookmarkRepository;
import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.Timeline.Domain.Timeline;
import Project.OpenBook.Domain.Timeline.Repo.TimelineRepository;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import Project.OpenBook.Domain.Topic.Repo.TopicRepository;
import Project.OpenBook.Handler.Exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static Project.OpenBook.Constants.ErrorCode.TIMELINE_NOT_FOUND;
import static Project.OpenBook.Constants.ErrorCode.TOPIC_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class BookmarkService {
    private final BookmarkRepository bookmarkRepository;
    private final TopicRepository topicRepository;
    private final TimelineRepository timelineRepository;


    @Transactional
    public void addBookmark(Customer customer, BookmarkDto dto) {

        String topicTitle = dto.getTopicTitle();
        Long timelineId = dto.getId();

        Bookmark bookmark;
        if (topicTitle != null) {
            Topic topic = topicRepository.findTopicByTitle(topicTitle).orElseThrow(() -> {
                throw new CustomException(TOPIC_NOT_FOUND);
            });

            bookmark = new Bookmark(customer, topic);
        }else{
            Timeline timeline = timelineRepository.findById(timelineId).orElseThrow(() -> {
                throw new CustomException(TIMELINE_NOT_FOUND);
            });

            bookmark = new Bookmark(customer, timeline);
        }

        bookmarkRepository.save(bookmark);
    }

    @Transactional
    public void deleteBookmark(Customer customer, BookmarkDto dto) {
        String topicTitle = dto.getTopicTitle();
        Long timelineId = dto.getId();

        if (topicTitle != null) {
            Topic topic = topicRepository.findTopicByTitle(topicTitle).orElseThrow(() -> {
                throw new CustomException(TOPIC_NOT_FOUND);
            });

            bookmarkRepository.findByCustomerAndTopic(customer, topic)
                    .ifPresent(bookmarkRepository::delete);
        }else{
            Timeline timeline = timelineRepository.findById(timelineId).orElseThrow(() -> {
                throw new CustomException(TIMELINE_NOT_FOUND);
            });

            bookmarkRepository.findByCustomerAndTimeline(customer, timeline)
                    .ifPresent(bookmarkRepository::delete);

        }


    }

    public List<String> queryBookmarks(Customer customer) {
        List<Bookmark> bookmarkList = bookmarkRepository.queryBookmarks(customer.getId());
        List<String> titleList = bookmarkList.stream().map(b -> b.getTopic().getTitle()).collect(Collectors.toList());
        return titleList;
    }
}
