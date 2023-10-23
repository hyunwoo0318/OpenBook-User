package Project.OpenBook.Domain.Search.KeywordSearch;

import Project.OpenBook.Domain.Search.ChapterSearch.ChapterSearch;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface KeywordSearchRepository extends ElasticsearchRepository<KeywordSearch, Long>, KeywordSearchRepositoryCustom {
}
