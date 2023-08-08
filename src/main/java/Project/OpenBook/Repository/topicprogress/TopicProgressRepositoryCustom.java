package Project.OpenBook.Repository.topicprogress;

import Project.OpenBook.Domain.TopicProgress;

import java.util.List;
import java.util.Optional;

public interface TopicProgressRepositoryCustom {

    public Optional<TopicProgress> queryTopicProgress(String topicTitle, Long customerId);

    public List<TopicProgress> queryTopicProgress(Long customerId);

    public List<TopicProgress> queryYTopicProgress(String topicTitle);
}
