package Project.OpenBook.Repository.topicprogress;

import Project.OpenBook.Domain.TopicProgress;

import java.util.List;
import java.util.Optional;

public interface TopicProgressRepositoryCustom {

    public Optional<TopicProgress> queryTopicProgress(Long customerId, String topicTitle );

    public List<TopicProgress> queryTopicProgresses(Long customerId, Integer chapterNum);
}
