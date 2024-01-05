package Project.OpenBook.Domain.Search.TopicSearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


public interface TopicSearchRepository extends ElasticsearchRepository<TopicSearch, Long>,
    TopicSearchRepositoryCustom {


}
