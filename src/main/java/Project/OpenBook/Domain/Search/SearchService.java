package Project.OpenBook.Domain.Search;

import Project.OpenBook.Domain.Search.KeywordSearch.KeywordSearch;
import Project.OpenBook.Domain.Search.KeywordSearch.KeywordSearchRepository;
import Project.OpenBook.Domain.Search.TopicSearch.TopicSearch;
import Project.OpenBook.Domain.Search.TopicSearch.TopicSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SearchService {
    private final TopicSearchRepository topicSearchRepository;
    private final KeywordSearchRepository keywordSearchRepository;

    public void searchByInput(String input) {
        List<TopicSearch> topicSearchList = topicSearchRepository.queryTopicSearchNameByInput(input);
        List<KeywordSearch> keywordSearchList = keywordSearchRepository.queryKeywordSearchNameByInput(input);
    }
}
