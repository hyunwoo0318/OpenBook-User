package Project.OpenBook.Domain.ExamQuestion;

import Project.OpenBook.Constants.ChoiceType;
import Project.OpenBook.Constants.Role;
import Project.OpenBook.Domain.Choice.Domain.Choice;
import Project.OpenBook.Domain.Choice.Repository.ChoiceRepository;
import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.Customer.Repository.CustomerRepository;
import Project.OpenBook.Domain.Description.Domain.Description;
import Project.OpenBook.Domain.ExamQuestion.Domain.ExamQuestion;
import Project.OpenBook.Domain.ExamQuestion.Repo.ExamQuestionRepository;
import Project.OpenBook.Domain.ExamQuestion.Service.dto.ExamQuestionChoiceDto;
import Project.OpenBook.Domain.ExamQuestion.Service.dto.ExamQuestionDto;
import Project.OpenBook.Domain.ExamQuestion.Service.dto.ExamQuestionInfoDto;
import Project.OpenBook.Domain.Question.Dto.QuestionChoiceDto;
import Project.OpenBook.Domain.Round.Domain.Round;
import Project.OpenBook.Domain.Round.Repo.RoundRepository;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import Project.OpenBook.Domain.Topic.Repo.TopicRepository;
import Project.OpenBook.Handler.Exception.error.ErrorMsgDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static Project.OpenBook.Constants.ErrorCode.QUESTION_NOT_FOUND;
import static Project.OpenBook.Constants.ErrorCode.ROUND_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = { "spring.config.location=classpath:application-test.yml" })
@ContextConfiguration
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ExamQuestionControllerTest {
    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    TopicRepository topicRepository;

    @Autowired
    RoundRepository roundRepository;

    @Autowired
    ExamQuestionRepository examQuestionRepository;

    @Autowired
    DescriptionRepository descriptionRepository;

    @Autowired
    ChoiceRepository choiceRepository;


    private String inputDescription = "data:image/png;base64,test-descritpion";

    private final String prefix = "http://localhost:";
    private String suffix;

    private Customer customer1;

    private Topic topic;
    private Round round;

    private ExamQuestion ex1, ex2;
    private Description desc1, desc2;

    private Choice ch1,ch2,ch3,ch4,ch5,ch6,ch7,ch8,ch9,ch10;

    private ExamQuestionDto dto1, dto2;

    private List<QuestionChoiceDto> choiceDtoList1, choiceDtoList2;

    String URL;

    private void initConfig() {
        URL = prefix + port + suffix;
    }

    @BeforeAll
    private void baseSetting() {
        customer1 = new Customer("customer1", passwordEncoder.encode("customer1"), Role.USER);
        customerRepository.save(customer1);

        topic =  new Topic("topic1");
        topicRepository.save(topic);

        round = new Round(123, 1);
        roundRepository.save(round);

        ex1 = new ExamQuestion(round, 1, 3, ChoiceType.String);
        ex2 = new ExamQuestion(round, 2, 4, ChoiceType.Image);
        examQuestionRepository.saveAll(Arrays.asList(ex1, ex2));

        desc1 = new Description("des1", "com1", topic, ex1);
        desc2 = new Description("des2", "com2", topic, ex2);
        descriptionRepository.saveAll(Arrays.asList(desc1,desc2));

        ch1 = new Choice(ChoiceType.String, "ch1", "commnet1", topic, ex1);
        ch2 = new Choice(ChoiceType.String, "ch2", "commnet2", topic, ex1);
        ch3 = new Choice(ChoiceType.String, "ch3", "commnet3", topic, ex1);
        ch4 = new Choice(ChoiceType.String, "ch4", "commnet4", topic, ex1);
        ch5 = new Choice(ChoiceType.String, "ch5", "commnet5", topic, ex1);
        ch6 = new Choice(ChoiceType.String, "ch6", "commnet6", topic, ex2);
        ch7 = new Choice(ChoiceType.String, "ch7", "commnet7", topic, ex2);
        ch8 = new Choice(ChoiceType.String, "ch8", "commnet8", topic, ex2);
        ch9 = new Choice(ChoiceType.String, "ch9", "commnet9", topic, ex2);
        ch10 = new Choice(ChoiceType.String, "ch10", "commnet10", topic, ex2);
        choiceRepository.saveAll(Arrays.asList(ch1, ch2, ch3, ch4, ch5, ch6, ch7, ch8, ch9, ch10));

        choiceDtoList1 = Arrays.asList(ch1, ch2, ch3, ch4, ch5).stream()
                .map(ch -> new QuestionChoiceDto(ch.getContent(), ch.getComment(), topic.getTitle(), ch.getId()))
                .collect(Collectors.toList());
        dto1 = new ExamQuestionDto(ex1.getNumber(), desc1.getContent(), desc1.getComment(), topic.getTitle(),
                ex1.getChoiceType().name(), ex1.getScore(),choiceDtoList1);

        choiceDtoList2 = Arrays.asList(ch6, ch7, ch8, ch9, ch10).stream()
                .map(ch -> new QuestionChoiceDto(ch.getContent(), ch.getComment(), topic.getTitle(), ch.getId()))
                .collect(Collectors.toList());
        dto2 = new ExamQuestionDto(ex2.getNumber(), desc2.getContent(), desc2.getComment(), topic.getTitle(),
                ex2.getChoiceType().name(), ex2.getScore(),choiceDtoList2 );
    }

    @AfterAll
    private void baseClear(){
        choiceRepository.deleteAllInBatch();
        descriptionRepository.deleteAllInBatch();
        examQuestionRepository.deleteAllInBatch();
        roundRepository.deleteAllInBatch();
        topicRepository.deleteAllInBatch();
        customerRepository.deleteAllInBatch();
    }

    @Nested
    @DisplayName("특정 회차의 모든 모의고사 문제 조회 - GET /rounds/{roundNumber}/questions")
    @TestInstance(PER_CLASS)
    public class queryRoundExamQuestions{

        @BeforeAll
        public void init(){
            suffix = "/rounds/";
            initConfig();
        }

        @DisplayName("문제 전체 조회 성공")
        @Test
        public void queryRoundExamQuestionsSuccess() {
            ResponseEntity<List<ExamQuestionDto>> response = restTemplate
                    .withBasicAuth("customer1", "customer1")
                    .exchange(URL + round.getNumber() + "/questions", HttpMethod.GET, null,
                            new ParameterizedTypeReference<List<ExamQuestionDto>>() {});

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            List<ExamQuestionDto> examQuestionDtoList = response.getBody();
            assertThat(examQuestionDtoList.size()).isEqualTo(2);

            ExamQuestionDto ret1 = examQuestionDtoList.get(0);
            ExamQuestionDto ret2 = examQuestionDtoList.get(1);

            //examQuestion1 검증

            assertThat(ret1).usingRecursiveComparison()
                    .isEqualTo(dto1);

            //examQuestion2 검증

            assertThat(ret2).usingRecursiveComparison()
                    .isEqualTo(dto2);


        }

        @DisplayName("존재하지 않는 roundNumber를 입력하는 경우")
        @Test
        public void inputNotExistRoundNumber(){
            ResponseEntity<List<ErrorMsgDto>> response = restTemplate
                    .withBasicAuth("customer1", "customer1")
                    .exchange(URL +"-1/questions", HttpMethod.GET,
                            null, new ParameterizedTypeReference<List<ErrorMsgDto>>() {});

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).usingRecursiveComparison()
                    .isEqualTo(Arrays.asList(new ErrorMsgDto(ROUND_NOT_FOUND.getErrorMessage())));
        }
    }

    @Nested
    @DisplayName("문제 정보 조회 - GET /rounds/{roundNumber}/questions/{questionNumber}/info")
    @TestInstance(PER_CLASS)
    public class queryQuestionInfo{
        @BeforeAll
        public void init(){
            suffix = "/rounds/";
            initConfig();
        }

        @Test
        @DisplayName("문제 정보 조회 성공")
        public void queryQuestionInfoSuccess(){
            ResponseEntity<ExamQuestionInfoDto> response
                    = restTemplate
                    .withBasicAuth("customer1", "customer1")
                    .getForEntity(URL + round.getNumber() + "/questions/"+ex1.getNumber()+"/info", ExamQuestionInfoDto.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).usingRecursiveComparison()
                    .isEqualTo(new ExamQuestionInfoDto(ex1.getNumber(), desc1.getContent(), desc1.getComment(), topic.getTitle(), ex1.getChoiceType().name(), ex1.getScore()));
        }

        @Test
        @DisplayName("존재하지 않는 roundNumber 입력")
        public void inputNotExistRoundNumber(){
            ResponseEntity<List<ErrorMsgDto>> response = restTemplate
                    .withBasicAuth("customer1", "customer1")
                    .exchange(URL +"-1/questions/1/info", HttpMethod.GET,
                            null, new ParameterizedTypeReference<List<ErrorMsgDto>>() {});

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).usingRecursiveComparison()
                    .isEqualTo(Arrays.asList(new ErrorMsgDto(QUESTION_NOT_FOUND.getErrorMessage())));
        }

        @Test
        @DisplayName("존재하지 않는 questionNumber 입력")
        public void inputNotExistQuestionNumber(){
            ResponseEntity<List<ErrorMsgDto>> response = restTemplate
                    .withBasicAuth("customer1", "customer1")
                    .exchange(URL + round.getNumber() + "/questions/-1/info", HttpMethod.GET,
                            null, new ParameterizedTypeReference<List<ErrorMsgDto>>() {});

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).usingRecursiveComparison()
                    .isEqualTo(Arrays.asList(new ErrorMsgDto(QUESTION_NOT_FOUND.getErrorMessage())));
        }

    }

    @Nested
    @DisplayName("문제 선지 모두 조회 - GET /admin/rounds/{roundNumber}/questions/{questionNumber}/choices")
    @TestInstance(PER_CLASS)
    public class queryQuestionChoices{
        @BeforeAll
        public void init(){
            suffix = "/admin/rounds/";
            initConfig();
        }

        @Test
        @DisplayName("문제 선지 모두 조회 성공")
        public void queryQuestionChoicesSuccess(){
            ResponseEntity<ExamQuestionChoiceDto> response
                    = restTemplate
                    .withBasicAuth("customer1", "customer1")
                    .getForEntity(URL + round.getNumber() + "/questions/"+ex1.getNumber()+"/choices", ExamQuestionChoiceDto.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).usingRecursiveComparison()
                    .isEqualTo(new ExamQuestionChoiceDto(choiceDtoList1));
        }

        @Test
        @DisplayName("존재하지 않는 roundNumber 입력")
        public void inputNotExistRoundNumber(){
            ResponseEntity<List<ErrorMsgDto>> response = restTemplate
                    .withBasicAuth("customer1", "customer1")
                    .exchange(URL +"-1/questions/1/choices", HttpMethod.GET,
                            null, new ParameterizedTypeReference<List<ErrorMsgDto>>() {});

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).usingRecursiveComparison()
                    .isEqualTo(Arrays.asList(new ErrorMsgDto(QUESTION_NOT_FOUND.getErrorMessage())));
        }

        @Test
        @DisplayName("존재하지 않는 questionNumber 입력")
        public void inputNotExistQuestionNumber(){
            ResponseEntity<List<ErrorMsgDto>> response = restTemplate
                    .withBasicAuth("customer1", "customer1")
                    .exchange(URL + round.getNumber() + "/questions/-1/choices", HttpMethod.GET,
                            null, new ParameterizedTypeReference<List<ErrorMsgDto>>() {});

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).usingRecursiveComparison()
                    .isEqualTo(Arrays.asList(new ErrorMsgDto(QUESTION_NOT_FOUND.getErrorMessage())));
        }

    }

    @Nested
    @DisplayName("문제 정보 생성 - POST /admin/rounds/{roundNumber}/questions")
    @TestInstance(PER_CLASS)
    public class saveExamQuestion{

        @BeforeAll
        public void init(){
            suffix = "/admin/rounds/";
            initConfig();
        }

        @Test
        @DisplayName("문제 생성 성공")
        public void saveExamQuestionSuccess(){
            Integer questionNumber = 3;
            ExamQuestionInfoDto dto = new ExamQuestionInfoDto(questionNumber, inputDescription, "description comment1", topic.getTitle()
                    , ChoiceType.Image.name(), 2);

            ResponseEntity<Void> response = restTemplate.postForEntity(URL + round.getNumber() + "/questions", dto, Void.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            //examQuestion 체크
            ExamQuestion findExamQuestion = examQuestionRepository.queryExamQuestionWithDescriptionAndTopic(round.getNumber(), questionNumber).get();
            assertThat(findExamQuestion.getScore()).isEqualTo(2);
            assertThat(findExamQuestion.getChoiceType()).isEqualTo(ChoiceType.Image);
            assertThat(findExamQuestion.getNumber()).isEqualTo(questionNumber);

            //description 체크
            Description findDescription = findExamQuestion.getDescription();
            assertThat(findDescription.getContent()).isEqualTo(inputDescription);
            assertThat(findDescription.getComment()).isEqualTo("description comment1");
            assertThat(findDescription.getTopic().getTitle()).isEqualTo(topic.getTitle());

        }
    }
}
