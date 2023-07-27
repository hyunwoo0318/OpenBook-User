package Project.OpenBook.Repository.topicprogress;

import Project.OpenBook.Domain.TopicProgress;

public interface TopicProgressRepositoryCustom {

    public TopicProgress queryTopicProgress(String topicTitle, Long customerId);
}
