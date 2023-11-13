package Project.OpenBook.Domain.Search.KeywordSearch;

import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class KeywordSearchRepositoryCustomImpl implements KeywordSearchRepositoryCustom {

    private final ElasticsearchOperations elasticsearchOperations;
    @Override
    public List<KeywordSearch> queryKeywordSearchNameByInput(String input) {
        Criteria criteria = Criteria.where("name").contains(input)
                .or(Criteria.where("comment").contains(input));
        CriteriaQuery query = new CriteriaQuery(criteria);
        SearchHits<KeywordSearch> search = elasticsearchOperations.search(query, KeywordSearch.class);
        return search.stream()
                .map(t -> t.getContent())
                .collect(Collectors.toList());
    }

    @Override
    public List<KeywordSearch> queryKeywordSearchCommentByInput(String input) {
        Criteria criteria = Criteria.where("comment").contains(input);
        CriteriaQuery query = new CriteriaQuery(criteria);
        SearchHits<KeywordSearch> search = elasticsearchOperations.search(query, KeywordSearch.class);
        return search.stream()
                .map(t -> t.getContent())
                .collect(Collectors.toList());
    }
}
