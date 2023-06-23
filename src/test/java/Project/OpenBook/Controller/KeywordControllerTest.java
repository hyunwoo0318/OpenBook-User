package Project.OpenBook.Controller;

import Project.OpenBook.Domain.*;
import Project.OpenBook.Dto.error.ErrorDto;
import Project.OpenBook.Dto.error.ErrorMsgDto;
import Project.OpenBook.Dto.keyword.KeywordDto;
import Project.OpenBook.Repository.category.CategoryRepository;
import Project.OpenBook.Repository.chapter.ChapterRepository;
import Project.OpenBook.Repository.keyword.KeywordRepository;
import Project.OpenBook.Repository.topic.TopicRepository;
import Project.OpenBook.Repository.topickeyword.TopicKeywordRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.TestPropertySource;

import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = { "spring.config.location=classpath:application-test.yml" })
public class KeywordControllerTest {

    @LocalServerPort
    int port;
    @Autowired
    ChapterRepository chapterRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    TopicRepository topicRepository;
    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    KeywordRepository keywordRepository;

    @Autowired
    TopicKeywordRepository topicKeywordRepository;


    private Category c1;
    private Chapter ch1;
    private Topic t1;

    private final String keywordName = "keyword";
    private final String prefix = "http://localhost:";
    private String suffix;

    private Keyword k1,k2,k3;
    String URL;

    private void initConfig() {
        URL = prefix + port + suffix;
        restTemplate = restTemplate.withBasicAuth("admin1", "admin1");
        restTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    }

    private void baseSetting() {
        c1 = new Category("유물");
        categoryRepository.saveAndFlush(c1);

        ch1 = new Chapter("ch1", 1);
        chapterRepository.saveAndFlush(ch1);

        t1 = new Topic("title1", null, null, 0, 0, "detail1", ch1, c1);
        topicRepository.saveAndFlush(t1);

        k1 = new Keyword("k1");
        k2 = new Keyword("k2");
        k3 = new Keyword("k3");
        keywordRepository.saveAllAndFlush(Arrays.asList(k1, k2, k3));

        TopicKeyword topicKeyword1 = new TopicKeyword(t1, k1);
        TopicKeyword topicKeyword2 = new TopicKeyword(t1, k2);
        topicKeywordRepository.saveAndFlush(topicKeyword1);
        topicKeywordRepository.saveAndFlush(topicKeyword2);
    }

    private void baseClear() {
        topicKeywordRepository.deleteAllInBatch();
        keywordRepository.deleteAllInBatch();
        topicRepository.deleteAllInBatch();
        chapterRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
    }

    @Nested
    @DisplayName("전체 키워드 조회 - GET /keywords")
    @TestInstance(PER_CLASS)
    public class queryKeywords{
        @BeforeAll
        public void init(){
            suffix = "/keywords";
            initConfig();
        }

        @AfterEach
        public void clear(){
            baseClear();
        }

        @BeforeEach
        public void setting() {
            baseSetting();

        }
        @DisplayName("전체 키워드 조회 성공")
        @Test
        public void queryKeywordsSuccess() {
            ResponseEntity<List<String>> response = restTemplate.exchange(URL, HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<String>>() {
                    });

            List<String> body = response.getBody();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(body.size()).isEqualTo(3);
            assertThat(body.containsAll(Set.of("k1", "k2", "k3")));
        }

    }

    @Nested
    @DisplayName("특정 키워드를 가지는 모든 토픽 조회 - GET /keywords/{keywordName}/topics")
    @TestInstance(PER_CLASS)
    public class queryKeywordTopics{
        @BeforeAll
        public void init(){
            suffix = "/keywords/";
            initConfig();
        }

        @AfterEach
        public void clear(){
            baseClear();
        }

        @BeforeEach
        public void setting() {
            baseSetting();
            Topic t2 = new Topic("title2", null, null, 0, 0, "detail2", ch1, c1);
            topicRepository.saveAndFlush(t2);
            TopicKeyword topicKeyword = new TopicKeyword(t2, k2);
            topicKeywordRepository.saveAndFlush(topicKeyword);
        }

        @DisplayName("특정 키워드를 가지는 모든 토픽 조회 성공")
        @Test
        public void queryKeywordsSuccess() {
            ResponseEntity<List<String>> response = restTemplate.exchange(URL + "k1/topics", HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<String>>() {
                    });

            List<String> body = response.getBody();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(body.size()).isEqualTo(1);
            assertThat(body.containsAll(Set.of("title1")));
        }

    }

    @Nested
    @DisplayName("키워드 생성 - POST /keywords")
    @TestInstance(PER_CLASS)
    public class createKeywords{
        @BeforeAll
        public void init(){
            suffix = "/admin/keywords";
            initConfig();
        }

        @AfterEach
        public void clear(){
            baseClear();
        }

        @BeforeEach
        public void setting() {
            baseSetting();

        }
        @DisplayName("키워드 생성 성공")
        @Test
        public void queryKeywordsSuccess() {
            String newKeywordName = "newKeyword";
            KeywordDto keywordDto = new KeywordDto(newKeywordName);
            ResponseEntity<Void> response = restTemplate.postForEntity(URL, keywordDto, Void.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(keywordRepository.findAll().size()).isEqualTo(4);
            assertThat(keywordRepository.findByName(newKeywordName).isPresent()).isTrue();
        }

        @DisplayName("키워드 생성 실패")
        @Test
        public void queryKeywordsFail(){
            //키워드 이름을 적지 않은 경우
            KeywordDto wrongDto1 = new KeywordDto();

            //이미 존재하는 키워드와 이름이 중복되는 경우
            KeywordDto wrongDto2 = new KeywordDto("k1");

            ResponseEntity<List<ErrorDto>> response1 = restTemplate.exchange(URL, HttpMethod.POST, new HttpEntity<>(wrongDto1), new ParameterizedTypeReference<List<ErrorDto>>() {
            });
            ResponseEntity<ErrorMsgDto> response2 = restTemplate.exchange(URL, HttpMethod.POST, new HttpEntity<>(wrongDto2), ErrorMsgDto.class);

            List<ErrorDto> body1 = response1.getBody();
            ErrorMsgDto body2 = response2.getBody();

            assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(body1.get(0)).usingRecursiveComparison().isEqualTo(new ErrorDto("name", "키워드 이름을 입력해주세요"));

            assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(body2).usingRecursiveComparison().isEqualTo(new ErrorMsgDto("중복된 키워드 입니다."));
        }

    }

    @Nested
    @DisplayName("키워드 삭제 - DELETE /keywords")
    @TestInstance(PER_CLASS)
    public class deleteKeywords{
        @BeforeAll
        public void init(){
            suffix = "/admin/keywords/";
            initConfig();
        }

        @AfterEach
        public void clear(){
            baseClear();
        }

        @BeforeEach
        public void setting() {
            baseSetting();

        }
        @DisplayName("키워드 삭제 성공")
        @Test
        public void queryKeywordsSuccess() {
            ResponseEntity<Void> response = restTemplate.exchange(URL + "k3", HttpMethod.DELETE, null, Void.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(keywordRepository.findAll().size()).isEqualTo(2);
            assertThat(keywordRepository.findByName("k3").isPresent()).isFalse();
        }

        @DisplayName("키워드 삭제 실패")
        @Test
        public void queryKeywordsFail(){
            //존재하지 않는 키워드 삭제 요청
            ResponseEntity<ErrorMsgDto> response1 = restTemplate.exchange(URL + "wrongKeywordName", HttpMethod.DELETE,null, ErrorMsgDto.class);
            //토픽이 존재하는 키워드 삭제 요청
            ResponseEntity<ErrorMsgDto> response2 = restTemplate.exchange(URL + "k1", HttpMethod.DELETE, null, ErrorMsgDto.class);

            ErrorMsgDto body1 = response1.getBody();
            ErrorMsgDto body2 = response2.getBody();

            assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(body1).usingRecursiveComparison().isEqualTo(new ErrorMsgDto("존재하지 않는 키워드 이름입니다."));

            assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(body2).usingRecursiveComparison().isEqualTo(new ErrorMsgDto("해당 키워드를 가지고 있는 토픽이 존재합니다."));
        }

    }
}
