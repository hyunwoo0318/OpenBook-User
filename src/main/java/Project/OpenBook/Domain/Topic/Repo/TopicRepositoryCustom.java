package Project.OpenBook.Domain.Topic.Repo;

import Project.OpenBook.Domain.Topic.Domain.Topic;

import java.util.List;
import java.util.Optional;

public interface  TopicRepositoryCustom {

    public List<String> queryWrongTopicTitle(String topicTitle, int size);

    public List<Topic> queryTopicsByTopicTitleList(List<String> topicTitleList);

    public Optional<Topic> queryTopicWithCategoryChapter(String topicTitle);

    public Optional<Topic> queryTopicWithCategory(String topicTitle);
}
