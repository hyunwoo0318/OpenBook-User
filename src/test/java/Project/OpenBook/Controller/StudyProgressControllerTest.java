package Project.OpenBook.Controller;

import Project.OpenBook.Constants.ErrorCode;
import Project.OpenBook.Constants.Role;
import Project.OpenBook.Domain.*;
import Project.OpenBook.Dto.ChapterProgressAddDto;
import Project.OpenBook.Dto.TopicProgressAddDto;
import Project.OpenBook.Dto.error.ErrorDto;
import Project.OpenBook.Dto.error.ErrorMsgDto;
import Project.OpenBook.Repository.category.CategoryRepository;
import Project.OpenBook.Repository.chapter.ChapterRepository;
import Project.OpenBook.Repository.chapterprogress.ChapterProgressRepository;
import Project.OpenBook.Repository.customer.CustomerRepository;
import Project.OpenBook.Repository.topic.TopicRepository;
import Project.OpenBook.Repository.topicprogress.TopicProgressRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.convert.DataSizeUnit;
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

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = { "spring.config.location=classpath:application-test.yml" })
public class StudyProgressControllerTest {

    @LocalServerPort
    int port;
    @Autowired
    ChapterRepository chapterRepository;
    @Autowired
    TopicRepository topicRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    TestRestTemplate restTemplate;
    @Autowired
    TopicProgressRepository topicProgressRepository;
    @Autowired
    ChapterProgressRepository chapterProgressRepository;
    @Autowired
    CustomerRepository customerRepository;

    private final String prefix = "http://localhost:";
    private String suffix;
    String URL;

    private Chapter ch1, ch2;
    private Topic t1,t2;
    private Customer c1,c2;

    private void initConfig() {
        URL = prefix + port + suffix;
        restTemplate = restTemplate.withBasicAuth("admin1", "admin1");
        restTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    }

    private void baseSetting(){
        ch1 = new Chapter("c1", 1);
        ch2 = new Chapter("c2",2);
        chapterRepository.saveAllAndFlush(Arrays.asList(ch1, ch2));

        Category category = new Category("c1");
        categoryRepository.saveAndFlush(category);

        c1 = new Customer("c1", 23, 0, Role.USER, "Naver", "id1", false);
        c2 = new Customer("c2", 23, 0, Role.USER, "Naver", "id2", false);
        customerRepository.saveAllAndFlush(Arrays.asList(c1, c2));

        t1 = new Topic("title1", 100, 200, false, false, 0, 0, "detail1", ch1, category);
        t2 = new Topic("title2", 300, 400, false, false, 0, 0, "detail2", ch1, category);
        topicRepository.saveAllAndFlush(Arrays.asList(t1, t2));

        ChapterProgress chapterProgress = new ChapterProgress(c1, ch1);
        chapterProgressRepository.saveAndFlush(chapterProgress);
        chapterProgress.updateWrongCount(3);
        chapterProgressRepository.saveAndFlush(chapterProgress);

        TopicProgress topicProgress = new TopicProgress(c1, t1);
        topicProgressRepository.saveAndFlush(topicProgress);
        topicProgress.updateWrongCount(3);
        topicProgressRepository.saveAndFlush(topicProgress);
    }

    private void baseClear(){
        topicProgressRepository.deleteAllInBatch();
        chapterProgressRepository.deleteAllInBatch();
        customerRepository.deleteAllInBatch();
        topicRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
        chapterRepository.deleteAllInBatch();
    }

    @Nested
    @DisplayName("단원 학습 정보 입력 - POST /admin/progress/chapter")
    @TestInstance(PER_CLASS)
    public class addChapterProgress{
        @BeforeAll
        public void init(){
            suffix = "/admin/progress/chapter";
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

        @DisplayName("단원 학습 정보 입력 성공 - 처음 학습한 경우")
        @Test
        public void addChapterProgressSuccessFirst() {
            ChapterProgressAddDto dto = new ChapterProgressAddDto(c2.getId(), ch1.getNumber(), 5);

            ResponseEntity<Void> response = restTemplate.postForEntity(URL, dto, Void.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            ChapterProgress chapterProgress = chapterProgressRepository.queryChapterProgress(c2.getId(), ch1.getNumber());
            assertThat(chapterProgress).isNotNull();
            assertThat(chapterProgress.getWrongCount()).isEqualTo(5);
        }

        @DisplayName("단원 학습 정보 입력 성공 - 이전에 학습한적이 있는 경우")
        @Test
        public void addChapterProgressSuccessNotFirst() {
            ChapterProgressAddDto dto = new ChapterProgressAddDto(c1.getId(), ch1.getNumber(), 5);
            ResponseEntity<Void> response = restTemplate.postForEntity(URL, dto, Void.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            ChapterProgress chapterProgress = chapterProgressRepository.queryChapterProgress(c1.getId(), ch1.getNumber());
            assertThat(chapterProgress.getWrongCount()).isEqualTo(8); // 기존 3 + 추가된 5
        }

        @DisplayName("단원 학습 정보 입력 실패 - DTO Validation")
        @Test
        public void addChapterProgressFailWrongDto() {
            //필수조건인 회원아이디, 단원번호 생략
            ChapterProgressAddDto dto = new ChapterProgressAddDto();
            ResponseEntity<List<ErrorDto>> response = restTemplate.exchange(URL, HttpMethod.POST, new HttpEntity<>(dto), new ParameterizedTypeReference<List<ErrorDto>>() {
            });

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody().size()).isEqualTo(2);
        }

        @DisplayName("단원 학습 정보 입력 실패 - 존재하지 않는 회원아이디 입력")
        @Test
        public void addChapterProgressFailNotFoundCustomerId(){
            ChapterProgressAddDto dto = new ChapterProgressAddDto(-1L, ch1.getNumber(), 5);
            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL, HttpMethod.POST, new HttpEntity<>(dto), new ParameterizedTypeReference<List<ErrorMsgDto>>() {
            });

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody().size()).isEqualTo(1);
            assertThat(response.getBody().get(0)).usingRecursiveComparison().isEqualTo(new ErrorMsgDto(ErrorCode.CUSTOMER_NOT_FOUND.getErrorMessage()));
        }

        @DisplayName("단원 학습 정보 입력 실패 - 존재하지 않는 단원 번호 입력")
        @Test
        public void addChapterProgressFailNotFoundChapterNum(){
            ChapterProgressAddDto dto = new ChapterProgressAddDto(c1.getId(), -1, 5);
            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL, HttpMethod.POST, new HttpEntity<>(dto), new ParameterizedTypeReference<List<ErrorMsgDto>>() {
            });

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody().size()).isEqualTo(1);
            assertThat(response.getBody().get(0)).usingRecursiveComparison().isEqualTo(new ErrorMsgDto(ErrorCode.CHAPTER_NOT_FOUND.getErrorMessage()));
        }
    }

    @Nested
    @DisplayName("주제 학습 정보 입력 - POST /admin/progress/topic")
    @TestInstance(PER_CLASS)
    public class addTopicProgress{
        @BeforeAll
        public void init(){
            suffix = "/admin/progress/topic";
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

        @DisplayName("주제 학습 정보 입력 성공 - 처음 학습한 경우")
        @Test
        public void addTopicProgressSuccessFirst() {
            TopicProgressAddDto dto = new TopicProgressAddDto(c2.getId(), t1.getTitle(), 5);

            ResponseEntity<Void> response = restTemplate.postForEntity(URL, dto, Void.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            TopicProgress topicProgress = topicProgressRepository.queryTopicProgress(t1.getTitle(), c2.getId());
            assertThat(topicProgress).isNotNull();
            assertThat(topicProgress.getWrongCount()).isEqualTo(5);
        }

        @DisplayName("주제 학습 정보 입력 성공 - 이전에 학습한적이 있는 경우")
        @Test
        public void addTopicProgressSuccessNotFirst() {
            TopicProgressAddDto dto = new TopicProgressAddDto(c1.getId(), t1.getTitle(), 5);
            ResponseEntity<Void> response = restTemplate.postForEntity(URL, dto, Void.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            TopicProgress topicProgress = topicProgressRepository.queryTopicProgress(t1.getTitle(), c1.getId());
            assertThat(topicProgress.getWrongCount()).isEqualTo(8); // 기존 3 + 추가된 5
        }

        @DisplayName("주제 학습 정보 입력 실패 - DTO Validation")
        @Test
        public void addTopicProgressFailWrongDto() {
            //필수조건인 회원아이디, 토픽제목 생략
            TopicProgressAddDto dto = new TopicProgressAddDto();
            ResponseEntity<List<ErrorDto>> response = restTemplate.exchange(URL, HttpMethod.POST, new HttpEntity<>(dto), new ParameterizedTypeReference<List<ErrorDto>>() {
            });

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody().size()).isEqualTo(2);
        }

        @DisplayName("주제 학습 정보 입력 실패 - 존재하지 않는 회원아이디 입력")
        @Test
        public void addTopicProgressFailNotFoundCustomerId(){
            TopicProgressAddDto dto = new TopicProgressAddDto(-1L, t1.getTitle(), 5);
            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL, HttpMethod.POST, new HttpEntity<>(dto), new ParameterizedTypeReference<List<ErrorMsgDto>>() {
            });

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody().size()).isEqualTo(1);
            assertThat(response.getBody().get(0)).usingRecursiveComparison().isEqualTo(new ErrorMsgDto(ErrorCode.CUSTOMER_NOT_FOUND.getErrorMessage()));
        }

        @DisplayName("주제 학습 정보 입력 실패 - 존재하지 않는 주제 제목 입력")
        @Test
        public void addChapterProgressFailNotFoundChapterNum(){
            TopicProgressAddDto dto = new TopicProgressAddDto(c1.getId(), "title-2-2-2-2-2", 5);
            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL, HttpMethod.POST, new HttpEntity<>(dto), new ParameterizedTypeReference<List<ErrorMsgDto>>() {
            });

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody().size()).isEqualTo(1);
            assertThat(response.getBody().get(0)).usingRecursiveComparison().isEqualTo(new ErrorMsgDto(ErrorCode.TOPIC_NOT_FOUND.getErrorMessage()));
        }
    }



}
