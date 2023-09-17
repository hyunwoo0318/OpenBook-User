package Project.OpenBook.Domain.Sentence;

import Project.OpenBook.Constants.Role;
import Project.OpenBook.Domain.Chapter.Domain.Chapter;
import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.Customer.Repository.CustomerRepository;
import Project.OpenBook.Domain.Sentence.Dto.SentenceCreateDto;
import Project.OpenBook.Domain.Sentence.Dto.SentenceUpdateDto;
import Project.OpenBook.Handler.Exception.error.ErrorDto;
import Project.OpenBook.Handler.Exception.error.ErrorMsgDto;
import Project.OpenBook.Domain.Sentence.Repository.SentenceRepository;
import Project.OpenBook.Domain.Sentence.Domain.Sentence;
import Project.OpenBook.Domain.Topic.Repo.TopicRepository;
import Project.OpenBook.Domain.Topic.Domain.Topic;
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
    PasswordEncoder passwordEncoder;

    @Autowired
    TopicRepository topicRepository;

    @Autowired
    SentenceRepository sentenceRepository;

    @Autowired
    CustomerRepository customerRepository;
    private final String prefix = "http://localhost:";

    private String URL,suffix;

    Topic t1, t2;

    Sentence s1, s2, s3, s4, s5;
    Customer customer1;

    private void initConfig(){
        URL = prefix + port + suffix;
        restTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    }

    private void baseSetting(){

        customer1 = new Customer("customer1", passwordEncoder.encode("customer1"), Role.USER);
        customerRepository.save(customer1);


        t1 = new Topic("t1");
        t2 = new Topic("t2");

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
        customerRepository.deleteAllInBatch();
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
            ResponseEntity<Void> response = restTemplate
                    .withBasicAuth("customer1", "customer1")
                    .postForEntity(URL, dto, Void.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            assertThat(sentenceRepository.findAll().size()).isEqualTo(prevSize + 1);
        }

        @DisplayName("문장 추가 실패 - DTO Validation")
        @Test
        public void createSentenceFailWrongDto() {

            //이름과 토픽제목을 입력하지 않음
            SentenceCreateDto dto = new SentenceCreateDto();
            ResponseEntity<List<ErrorMsgDto>> response = restTemplate
                    .withBasicAuth("customer1", "customer1")
                    .exchange(URL, HttpMethod.POST, new HttpEntity<>(dto), new ParameterizedTypeReference<List<ErrorMsgDto>>() {
            });

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody().size()).isEqualTo(2);
        }

        @DisplayName("문장 추가 실패 - 존재하지 않는 토픽 제목 입력")
        @Test
        public void createSentenceFailNotExistTopic(){
            SentenceCreateDto dto = new SentenceCreateDto("newSentence1", "title-1-1");

            ResponseEntity<List<ErrorMsgDto>> response = restTemplate
                    .withBasicAuth("customer1", "customer1")
                    .exchange(URL,HttpMethod.POST,
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

            ResponseEntity<Void> response = restTemplate
                    .withBasicAuth("customer1", "customer1")
                    .exchange(URL + sentenceId, HttpMethod.PATCH, new HttpEntity<>(dto), Void.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(sentenceRepository.findById(sentenceId).get().getName()).isEqualTo(afterContent);
            assertThat(sentenceRepository.findAll().size()).isEqualTo(prevSize);
        }

        @DisplayName("문장 수정 실패 - DTO Validation")
        @Test
        public void updateSentenceWrongDto(){
            SentenceUpdateDto dto = new SentenceUpdateDto();
            ResponseEntity<List<ErrorDto>> response = restTemplate
                    .withBasicAuth("customer1", "customer1")
                    .exchange(URL + sentenceId, HttpMethod.PATCH, new HttpEntity<>(dto), new ParameterizedTypeReference<List<ErrorDto>>() {
            });

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody().size()).isEqualTo(1);
        }

        @DisplayName("문장 수정 실패 - 존재하지 않는 문장id 입력")
        @Test
        public void updateSentenceNotExistSentence(){
            SentenceUpdateDto dto = new SentenceUpdateDto("newContent1");

            ResponseEntity<List<ErrorMsgDto>> response = restTemplate
                    .withBasicAuth("customer1", "customer1")
                    .exchange(URL + "123123", HttpMethod.PATCH,
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
            ResponseEntity<Void> response = restTemplate
                    .withBasicAuth("customer1", "customer1")
                    .exchange(URL + sentenceId, HttpMethod.DELETE, null, Void.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

            assertThat(sentenceRepository.findById(sentenceId).isEmpty()).isTrue();
            assertThat(sentenceRepository.findAll().size()).isEqualTo(prevSize - 1);
        }


        @DisplayName("문장 삭제 실패 - 존재하지 않는 문장id 입력")
        @Test
        public void deleteSentenceFailNotExistSentence(){
            ResponseEntity<List<ErrorMsgDto>> response = restTemplate
                    .withBasicAuth("customer1", "customer1")
                    .exchange(URL + "123123", HttpMethod.DELETE,
                    null, new ParameterizedTypeReference<List<ErrorMsgDto>>() {});

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto(SENTENCE_NOT_FOUND.getErrorMessage())));
        }
    }
}
