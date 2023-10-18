package Project.OpenBook.Config;

import Project.OpenBook.Constants.Role;
import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.Customer.Repository.CustomerRepository;
import Project.OpenBook.Domain.KeywordAssociation.KeywordAssociationRepository;
import Project.OpenBook.Domain.Search.TopicSearch.TopicSearch;
import Project.OpenBook.Domain.Search.TopicSearch.TopicSearchRepository;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import Project.OpenBook.Domain.Topic.Repo.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class InitConfig {

    private final TopicSearchRepository topicSearchRepository;
    private final TopicRepository topicRepository;
    private final CustomerRepository customerRepository;
    private final KeywordAssociationRepository keywordAssociationRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * ElasticSearch를 위한 init
     * 각 topic의 title, id를 저장
     */
    @Bean
    public void initElasticSearchIndex() {
        List<Topic> topicList = topicRepository.findAll();
        topicSearchRepository.deleteAll();

        List<TopicSearch> topicSearchList = topicList.stream()
                .map(topic -> new TopicSearch(topic.getDetail(), topic.getTitle(), topic.getId()))
                .collect(Collectors.toList());

        topicSearchRepository.saveAll(topicSearchList);
    }

    /**
     * 기본 관리자 아이디 세팅
     */
    @Bean
    public void initAdmin(){
        if(customerRepository.findByNickName("admin1").isEmpty()){
            Customer admin1 = new Customer("admin1", passwordEncoder.encode("admin1"), Role.ADMIN);
            Customer admin2 = new Customer("admin2", passwordEncoder.encode("admin2"), Role.ADMIN);
            customerRepository.saveAll(Arrays.asList(admin1, admin2));
        }
    }

    /**
     * 키워드 연관성 관련 세팅     *
     */

    @Bean
    public void initKeywordAssociation() {

    }

}
