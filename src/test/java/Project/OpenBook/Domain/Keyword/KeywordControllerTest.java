package Project.OpenBook.Domain.Keyword;

import Project.OpenBook.Constants.Role;
import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.Customer.Repository.CustomerRepository;
import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import Project.OpenBook.Domain.Keyword.Service.Dto.KeywordCreateDto;
import Project.OpenBook.Domain.Keyword.Service.Dto.KeywordUserDto;
import Project.OpenBook.Domain.Keyword.Repository.KeywordRepository;
import Project.OpenBook.Domain.StudyHistory.ChapterSection.Repository.ChapterSectionRepository;
import Project.OpenBook.Domain.StudyHistory.TopicProgress.Repository.TopicProgressRepository;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import Project.OpenBook.Domain.Topic.Repo.TopicRepository;
import Project.OpenBook.Handler.Exception.error.ErrorMsgDto;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import java.util.*;

import static Project.OpenBook.Constants.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = { "spring.config.location=classpath:application-test.yml" })
public class KeywordControllerTest {

    @LocalServerPort
    int port;
    @Autowired
    TopicRepository topicRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    TestRestTemplate restTemplate;
    @Autowired
    KeywordRepository keywordRepository;

    @Autowired
    CustomerRepository customerRepository;


    private final String imageUrl = "../TestImage/";
    private Topic t1,t2,t3;
    private final String prefix = "http://localhost:";
    private String suffix;

    private Keyword k1,k2,k3;
    String URL;

    private void initConfig() {
        URL = prefix + port + suffix;
        restTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    }

    private void baseSetting() {
        Customer customer1 = new Customer("customer1", passwordEncoder.encode("customer1"), Role.USER);
        customerRepository.save(customer1);

        t1 = new Topic("title1");
        topicRepository.saveAndFlush(t1);

        k1 = new Keyword("k1","c1",t1, null);
        k2 = new Keyword("k2","c2", t1,null);
        k3 = new Keyword("k3","c3",t1,null);
        keywordRepository.saveAllAndFlush(Arrays.asList(k1, k2, k3));
    }

    private void baseClear() {
        keywordRepository.deleteAllInBatch();
        topicRepository.deleteAllInBatch();
        customerRepository.deleteAllInBatch();
    }



    @Nested
    @DisplayName("키워드 생성 - POST /keywords")
    @TestInstance(PER_CLASS)
    public class createKeywords{

        private int prevSize;
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
            prevSize = keywordRepository.findAll().size();

        }
        @DisplayName("키워드 생성 성공")
        @Test
        public void createKeywordsSuccess() {
            KeywordCreateDto keywordDto = new KeywordCreateDto("newName1", "newComment1", t1.getTitle(), null);

            ResponseEntity<Void> response = restTemplate
                    .withBasicAuth("customer1", "customer1")
                    .postForEntity(URL, keywordDto, Void.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(keywordRepository.findAll().size()).isEqualTo(prevSize + 1);
            Optional<Keyword> keywordOptional = keywordRepository.queryByNameInTopic(keywordDto.getName(), t1.getTitle());
            Keyword keyword = keywordOptional.orElseThrow();
            assertThat(keyword.getComment()).isEqualTo("newComment1");
            assertThat(keyword.getName()).isEqualTo("newName1");
        }

        @DisplayName("키워드 생성 실패 - DTO validation")
        @Test
        public void createKeywordsFailWrongDto(){
            //키워드 이름, 토픽 제목을 적지 않은 경우
            KeywordCreateDto wrongDto = new KeywordCreateDto();

            ResponseEntity<List<ErrorMsgDto>> response = restTemplate
                    .withBasicAuth("customer1", "customer1")
                    .exchange(URL, HttpMethod.POST,
                    new HttpEntity<>(wrongDto), new ParameterizedTypeReference<List<ErrorMsgDto>>() {
            });

            List<ErrorMsgDto> body = response.getBody();

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(body.size()).isEqualTo(2);

            assertThat(keywordRepository.findAll().size()).isEqualTo(prevSize);
        }

        @DisplayName("키워드 생성 실패 - 키워드의 이름 중복")
        @Test
        public void createKeywordsFailDupName() {
           //이미 존재하는 키워드와 이름이 중복되는 경우
            KeywordCreateDto wrongDto = new KeywordCreateDto("k1", "c1", t1.getTitle(),"");

            ResponseEntity<List<ErrorMsgDto>> response = restTemplate
                    .withBasicAuth("customer1", "customer1")
                    .exchange(URL, HttpMethod.POST,
                    new HttpEntity<>(wrongDto), new ParameterizedTypeReference<List<ErrorMsgDto>>() {});

            List<ErrorMsgDto> body = response.getBody();

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(body).usingRecursiveComparison()
                    .isEqualTo(Arrays.asList(new ErrorMsgDto(DUP_KEYWORD_NAME.getErrorMessage())));

            assertThat(keywordRepository.findAll().size()).isEqualTo(prevSize);
        }

        @DisplayName("키워드 생성 실패 - 존재하지 않는 토픽제목 입력")
        @Test
        public void createKeywordsFailWrongTopicTitle() {
            //존재하지 않는 토픽제목 입력
            KeywordCreateDto wrongDto = new KeywordCreateDto("k1", "c1", "wrongTitle1","");

            ResponseEntity<List<ErrorMsgDto>> response = restTemplate
                    .withBasicAuth("customer1", "customer1")
                    .exchange(URL, HttpMethod.POST,
                    new HttpEntity<>(wrongDto), new ParameterizedTypeReference<List<ErrorMsgDto>>() {});

            List<ErrorMsgDto> body = response.getBody();

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(body).usingRecursiveComparison()
                    .isEqualTo(Arrays.asList(new ErrorMsgDto(TOPIC_NOT_FOUND.getErrorMessage())));

            assertThat(keywordRepository.findAll().size()).isEqualTo(prevSize);
        }

    }

    @Nested
    @DisplayName("키워드 수정 - PATCH - admin/keywords/{keywordId}")
    @TestInstance(PER_CLASS)
    public class updateKeywords{
        private Long keywordId;
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
            keywordId = k1.getId();
        }
        @DisplayName("키워드 수정 성공")
        @Test
        public void updateKeywordsSuccess() {
            KeywordUserDto dto = new KeywordUserDto("newName", "newComment", null);
            ResponseEntity<Void> response = restTemplate
                    .withBasicAuth("customer1", "customer1")
                    .exchange(URL + keywordId, HttpMethod.PATCH,
                    new HttpEntity<>(dto), Void.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            Keyword updatedKeyword = keywordRepository.findById(keywordId).get();
            assertThat(updatedKeyword.getName()).isEqualTo("newName");
            assertThat(updatedKeyword.getComment()).isEqualTo("newComment");
            assertThat(updatedKeyword.getImageUrl()).isNull();
        }

        @DisplayName("키워드 수정 실패 - 존재하지 않는 키워드 삭제 요청")
        @Test
        public void updateKeywordsFail(){
            KeywordUserDto dto = new KeywordUserDto("newName", "newComment", null);
            ResponseEntity<List<ErrorMsgDto>> response = restTemplate
                    .withBasicAuth("customer1", "customer1")
                    .exchange(URL + "-123", HttpMethod.PATCH,
                            new HttpEntity<>(dto), new ParameterizedTypeReference<List<ErrorMsgDto>>() {});
            List<ErrorMsgDto> body = response.getBody();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(body).usingRecursiveComparison()
                    .isEqualTo(Arrays.asList(new ErrorMsgDto(KEYWORD_NOT_FOUND.getErrorMessage())));
        }
    }

    @Nested
    @DisplayName("키워드 삭제 - DELETE admin/keywords/{keywordId}")
    @TestInstance(PER_CLASS)
    public class deleteKeywords{

        private int prevSize;
        private Long keywordId;
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
            prevSize = keywordRepository.findAll().size();
            keywordId = k1.getId();
        }
        @DisplayName("키워드 삭제 성공")
        @Test
        public void queryKeywordsSuccess() {
            ResponseEntity<Void> response = restTemplate
                    .withBasicAuth("customer1", "customer1")
                    .exchange(URL + keywordId, HttpMethod.DELETE, null, Void.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(keywordRepository.queryByNameInTopic(k1.getName(), t1.getTitle()).isEmpty()).isTrue();
            assertThat(keywordRepository.findAll().size()).isEqualTo(prevSize - 1);
        }

        @DisplayName("키워드 삭제 실패")
        @Test
        public void queryKeywordsFail(){
            //존재하지 않는 키워드 삭제 요청
            ResponseEntity<List<ErrorMsgDto>> response = restTemplate
                    .withBasicAuth("customer1", "customer1")
                    .exchange(URL + "-123", HttpMethod.DELETE,
                    null, new ParameterizedTypeReference<List<ErrorMsgDto>>() {});
            List<ErrorMsgDto> body = response.getBody();


            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(body).usingRecursiveComparison()
                    .isEqualTo(Arrays.asList(new ErrorMsgDto(KEYWORD_NOT_FOUND.getErrorMessage())));
            assertThat(keywordRepository.findAll().size()).isEqualTo(prevSize);
        }

    }
}
