package Project.OpenBook.Controller;

import Project.OpenBook.Constants.ErrorCode;
import Project.OpenBook.Domain.*;
import Project.OpenBook.Dto.Sentence.SentenceCreateDto;
import Project.OpenBook.Dto.Sentence.SentenceDto;
import Project.OpenBook.Dto.Sentence.SentenceUpdateDto;
import Project.OpenBook.Dto.description.DescriptionCreateDto;
import Project.OpenBook.Dto.description.DescriptionUpdateDto;
import Project.OpenBook.Dto.error.ErrorDto;
import Project.OpenBook.Dto.error.ErrorMsgDto;
import Project.OpenBook.Repository.category.CategoryRepository;
import Project.OpenBook.Repository.chapter.ChapterRepository;
import Project.OpenBook.Repository.sentence.SentenceRepository;
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

import java.util.Arrays;
import java.util.List;

import static Project.OpenBook.Constants.ErrorCode.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = { "spring.config.location=classpath:application-test.yml" })
public class SentenceControllerTest {

    @LocalServerPort
    protected int port;

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    ChapterRepository chapterRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    TopicRepository topicRepository;

    @Autowired
    SentenceRepository sentenceRepository;

    private final String prefix = "http://localhost:";

    private String URL,suffix;

    Topic t1, t2;

    Sentence s1, s2, s3, s4, s5;

    private void initConfig(){
        URL = prefix + port + suffix;
        restTemplate = restTemplate.withBasicAuth("admin1", "admin1");
        restTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    }

    private void baseSetting(){
        Chapter ch1 = new Chapter("ch1", 1);
        Category c1 = new Category("c1");
        chapterRepository.save(ch1);
        categoryRepository.save(c1);

        t1 = new Topic("title1", null, null, 0, 0, "detail1", ch1, c1);
        t2 = new Topic("title2", null, null, 0, 0, "detail2", ch1, c1);

        topicRepository.save(t1);
        topicRepository.save(t2);

        /**
         * s1,2,3 -> topic1
         * c4,5 -> topic2
         */
        s1 = new Sentence("sentence1", t1);
        s2 = new Sentence("sentence2", t1);
        s3 = new Sentence("sentence3", t1);

        s4 = new Sentence("sentence4", t2);
        s5 = new Sentence("sentence5", t2);

        sentenceRepository.saveAllAndFlush(Arrays.asList(s1, s2, s3, s4, s5));
    }
    private void baseClear() {
        sentenceRepository.deleteAllInBatch();
        topicRepository.deleteAllInBatch();
        chapterRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
    }

    @Nested
    @DisplayName("문장 추가 - POST /admin/sentences/")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    public class createSentence{

        private int prevSize;
        @BeforeAll
        public void init(){
            suffix = "/admin/sentences/";
            initConfig();
        }

        @BeforeEach
        public void setting(){
            baseSetting();
            prevSize = sentenceRepository.findAll().size();
        }

        @AfterEach
        public void clear(){
            baseClear();
        }

        @DisplayName("문장 추가 성공")
        @Test
        public void createSentenceSuccess() {
            SentenceCreateDto dto = new SentenceCreateDto("newSentence1", t1.getTitle());
            ResponseEntity<Void> response = restTemplate.postForEntity(URL, dto, Void.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            assertThat(sentenceRepository.queryByTopicTitle(t1.getTitle()).size()).isEqualTo(4);
            assertThat(sentenceRepository.findAll().size()).isEqualTo(prevSize + 1);
        }

        @DisplayName("문장 추가 실패 - DTO Validation")
        @Test
        public void createSentenceFailWrongDto() {

            //이름과 토픽제목을 입력하지 않음
            SentenceCreateDto dto = new SentenceCreateDto();
            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL, HttpMethod.POST, new HttpEntity<>(dto), new ParameterizedTypeReference<List<ErrorMsgDto>>() {
            });

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody().size()).isEqualTo(2);
        }

        @DisplayName("문장 추가 실패 - 존재하지 않는 토픽 제목 입력")
        @Test
        public void createSentenceFailNotExistTopic(){
            SentenceCreateDto dto = new SentenceCreateDto("newSentence1", "title-1-1");

            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL,HttpMethod.POST,
                    new HttpEntity<>(dto), new ParameterizedTypeReference<List<ErrorMsgDto>>(){});

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto(TOPIC_NOT_FOUND.getErrorMessage())));
        }
    }

    @Nested
    @DisplayName("문장 수정 - PATCH /admin/sentences/{sentenceId}")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    public class updateSentence{

        private Long sentenceId;
        private int prevSize;
        @BeforeAll
        public void init(){
            suffix = "/admin/sentences/";
            initConfig();
        }

        @BeforeEach
        public void setting(){
            baseSetting();
            prevSize = sentenceRepository.findAll().size();
            sentenceId = s1.getId();
        }

        @AfterEach
        public void clear(){
            baseClear();
        }

        @DisplayName("문장 수정 성공")
        @Test
        public void updateSentenceSuccess() {
            String afterContent = "afterContent1";
            SentenceUpdateDto dto = new SentenceUpdateDto(afterContent);

            ResponseEntity<Void> response = restTemplate.exchange(URL + sentenceId, HttpMethod.PATCH, new HttpEntity<>(dto), Void.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

            assertThat(sentenceRepository.queryByTopicTitle(t1.getTitle()).size()).isEqualTo(3);
            assertThat(sentenceRepository.findById(sentenceId).get().getName()).isEqualTo(afterContent);
            assertThat(sentenceRepository.findAll().size()).isEqualTo(prevSize);
        }

        @DisplayName("문장 수정 실패 - DTO Validation")
        @Test
        public void updateSentenceWrongDto(){
            SentenceUpdateDto dto = new SentenceUpdateDto();
            ResponseEntity<List<ErrorDto>> response = restTemplate.exchange(URL + sentenceId, HttpMethod.PATCH, new HttpEntity<>(dto), new ParameterizedTypeReference<List<ErrorDto>>() {
            });

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody().size()).isEqualTo(1);
        }

        @DisplayName("문장 수정 실패 - 존재하지 않는 문장id 입력")
        @Test
        public void updateSentenceNotExistSentence(){
            SentenceUpdateDto dto = new SentenceUpdateDto("newContent1");

            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL + "123123", HttpMethod.PATCH,
                    new HttpEntity<>(dto), new ParameterizedTypeReference<List<ErrorMsgDto>>() {});

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody().size()).isEqualTo(1);
            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto(SENTENCE_NOT_FOUND.getErrorMessage())));
        }


    }

    @Nested
    @DisplayName("문장 삭제 - DELETE /admin/sentences/{sentenceId}")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    public class deleteSentence{

        private Long sentenceId;
        private int prevSize;
        @BeforeAll
        public void init(){
            suffix = "/admin/sentences/";
            initConfig();
        }

        @BeforeEach
        public void setting(){
            baseSetting();
            prevSize = sentenceRepository.findAll().size();
            sentenceId = s1.getId();
        }

        @AfterEach
        public void clear(){
            baseClear();
        }

        @DisplayName("문장 삭제 성공")
        @Test
        public void deleteSentenceSuccess() {
            ResponseEntity<Void> response = restTemplate.exchange(URL + sentenceId, HttpMethod.DELETE, null, Void.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

            assertThat(sentenceRepository.queryByTopicTitle(t1.getTitle()).size()).isEqualTo(2);
            assertThat(sentenceRepository.findById(sentenceId).isEmpty()).isTrue();
            assertThat(sentenceRepository.findAll().size()).isEqualTo(prevSize - 1);
        }


        @DisplayName("문장 삭제 실패 - 존재하지 않는 문장id 입력")
        @Test
        public void deleteSentenceFailNotExistSentence(){
            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL + "123123", HttpMethod.DELETE,
                    null, new ParameterizedTypeReference<List<ErrorMsgDto>>() {});

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto(SENTENCE_NOT_FOUND.getErrorMessage())));
        }
    }
}
