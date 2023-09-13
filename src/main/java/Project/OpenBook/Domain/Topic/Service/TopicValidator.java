package Project.OpenBook.Domain.Topic.Service;

import Project.OpenBook.Domain.Topic.Domain.Topic;
import Project.OpenBook.Domain.Topic.Repo.TopicRepository;
import Project.OpenBook.Handler.Exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static Project.OpenBook.Constants.ErrorCode.DUP_TOPIC_TITLE;
import static Project.OpenBook.Constants.ErrorCode.TOPIC_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class TopicValidator {
    private final TopicRepository topicRepository;

    public Topic checkTopic(String topicTitle) {
        return topicRepository.findTopicByTitle(topicTitle).orElseThrow(() -> {
            throw new CustomException(TOPIC_NOT_FOUND);
        });
    }

    public void checkDupTopicTitle(String topicTitle) {
        topicRepository.findTopicByTitle(topicTitle).ifPresent(t -> {
            throw new CustomException(DUP_TOPIC_TITLE);
        });
    }
}
