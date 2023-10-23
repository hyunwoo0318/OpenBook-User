package Project.OpenBook.Domain.Search.ChapterSearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ChapterSearchRepository extends ElasticsearchRepository<ChapterSearch, Long>, ChapterSearchRepositoryCustom {
}
