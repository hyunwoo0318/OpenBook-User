package Project.OpenBook.Domain.Search.TopicSearch;

import Project.OpenBook.Domain.Topic.Domain.Topic;

import java.util.List;

public interface TopicSearchRepositoryCustom {

    List<TopicSearch> queryTopicSearchNameByInput(String input);
}
