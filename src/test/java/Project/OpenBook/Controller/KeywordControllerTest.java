package Project.OpenBook.Controller;

import Project.OpenBook.Constants.ErrorCode;
import Project.OpenBook.Domain.*;
import Project.OpenBook.Dto.error.ErrorDto;
import Project.OpenBook.Dto.error.ErrorMsgDto;
import Project.OpenBook.Dto.keyword.KeywordCreateDto;
import Project.OpenBook.Dto.keyword.KeywordDto;
import Project.OpenBook.Dto.topic.TopicTitleDto;
import Project.OpenBook.Repository.category.CategoryRepository;
import Project.OpenBook.Repository.chapter.ChapterRepository;
import Project.OpenBook.Repository.keyword.KeywordRepository;
import Project.OpenBook.Repository.topic.TopicRepository;
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
import java.util.*;
import java.util.stream.Collectors;

import static Project.OpenBook.Constants.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = { "spring.config.location=classpath:application-test.yml" })
public class KeywordControllerTest {

    @LocalServerPort
    int port;

    private final String imageUrl = "../TestImage/";
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

        t1 = new Topic("title1", null, null, false,false,0, 0, "detail1", ch1, c1);
        topicRepository.saveAndFlush(t1);

        k1 = new Keyword("k1","c1",t1);
        k2 = new Keyword("k2","c2", t1);
        k3 = new Keyword("k3","c3",t1);
        keywordRepository.saveAllAndFlush(Arrays.asList(k1, k2, k3));
    }

    private void baseClear() {
        keywordRepository.deleteAllInBatch();
        topicRepository.deleteAllInBatch();
        chapterRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
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

            //TODO : 파일 삽입 테스트
            ResponseEntity<Void> response = restTemplate.postForEntity(URL, keywordDto, Void.class);

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

            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL, HttpMethod.POST,
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

            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL, HttpMethod.POST,
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

            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL, HttpMethod.POST,
                    new HttpEntity<>(wrongDto), new ParameterizedTypeReference<List<ErrorMsgDto>>() {});

            List<ErrorMsgDto> body = response.getBody();

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(body).usingRecursiveComparison()
                    .isEqualTo(Arrays.asList(new ErrorMsgDto(TOPIC_NOT_FOUND.getErrorMessage())));

            assertThat(keywordRepository.findAll().size()).isEqualTo(prevSize);
        }

    }

    //TODO : 키워드 수정 테스트

    @Nested
    @DisplayName("키워드 삭제 - DELETE /keywords/{keywordId}")
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
            ResponseEntity<Void> response = restTemplate.exchange(URL + keywordId, HttpMethod.DELETE, null, Void.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(keywordRepository.queryByNameInTopic(k1.getName(), t1.getTitle()).isEmpty()).isTrue();
            assertThat(keywordRepository.findAll().size()).isEqualTo(prevSize - 1);
        }

        @DisplayName("키워드 삭제 실패")
        @Test
        public void queryKeywordsFail(){
            //존재하지 않는 키워드 삭제 요청
            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL + "-123", HttpMethod.DELETE,
                    null, new ParameterizedTypeReference<List<ErrorMsgDto>>() {});
            List<ErrorMsgDto> body = response.getBody();


            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(body).usingRecursiveComparison()
                    .isEqualTo(Arrays.asList(new ErrorMsgDto(KEYWORD_NOT_FOUND.getErrorMessage())));
            assertThat(keywordRepository.findAll().size()).isEqualTo(prevSize);
        }

    }
}
