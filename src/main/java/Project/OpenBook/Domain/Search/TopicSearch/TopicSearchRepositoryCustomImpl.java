package Project.OpenBook.Domain.Search.TopicSearch;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;

@RequiredArgsConstructor
public class TopicSearchRepositoryCustomImpl implements TopicSearchRepositoryCustom {

    private final ElasticsearchOperations elasticSearchOperations;

    @Override
    public List<TopicSearch> queryTopicSearchNameByInput(String input) {
        Criteria criteria = Criteria.where("title").contains(input);
        CriteriaQuery query = new CriteriaQuery(criteria);
        SearchHits<TopicSearch> search = elasticSearchOperations.search(query, TopicSearch.class);
        return search.stream()
            .map(SearchHit::getContent)
            .collect(Collectors.toList());
    }
}
