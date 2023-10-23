package Project.OpenBook.Domain.Search.ChapterSearch;

import Project.OpenBook.Domain.Search.KeywordSearch.KeywordSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ChapterSearchRepositoryCustomImpl implements ChapterSearchRepositoryCustom {

    private final ElasticsearchOperations elasticsearchOperations;
    @Override
    public List<ChapterSearch> queryChapterSearchNameByInput(String input) {
        Criteria criteria = Criteria.where("title").contains(input);
        CriteriaQuery query = new CriteriaQuery(criteria);
        SearchHits<ChapterSearch> search = elasticsearchOperations.search(query, ChapterSearch.class);
        return search.stream()
                .map(cs -> cs.getContent())
                .collect(Collectors.toList());
    }
}
