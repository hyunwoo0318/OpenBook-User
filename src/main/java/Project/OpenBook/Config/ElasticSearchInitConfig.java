package Project.OpenBook.Config;

import Project.OpenBook.Constants.ElasticSearchIndexConst;
import Project.OpenBook.Domain.Search.TopicSearch.TopicSearch;
import Project.OpenBook.Domain.Search.TopicSearch.TopicSearchRepository;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import Project.OpenBook.Domain.Topic.Repo.TopicRepository;
import Project.OpenBook.Handler.Exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class ElasticSearchInitConfig {

    private final TopicSearchRepository topicSearchRepository;
    private final TopicRepository topicRepository;

    @Bean
    public void initElasticSearchIndex() {
        List<Topic> topicList = topicRepository.findAll();
        topicSearchRepository.deleteAll();

        List<TopicSearch> topicSearchList = topicList.stream()
                .map(topic -> new TopicSearch(topic.getDetail(), topic.getTitle(), topic.getId()))
                .collect(Collectors.toList());

        topicSearchRepository.saveAll(topicSearchList);

    }

}
