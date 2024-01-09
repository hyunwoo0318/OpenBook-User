package Project.OpenBook.Domain.Search.TopicSearch;


import java.util.List;

public interface TopicSearchRepositoryCustom {

    List<TopicSearch> queryTopicSearchNameByInput(String input);
}
