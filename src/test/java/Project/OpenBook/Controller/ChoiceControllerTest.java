package Project.OpenBook.Controller;//package Project.OpenBook.Controller;
//
//import Project.OpenBook.Category.Domain.Category;
//import Project.OpenBook.Chapter.Domain.Chapter;
//import Project.OpenBook.Choice.Choice;
//import Project.OpenBook.Topic.Domain.Topic;
//import Project.OpenBook.Dto.choice.ChoiceAddDto;
//import Project.OpenBook.Dto.choice.ChoiceDto;
//import Project.OpenBook.Dto.choice.ChoiceUpdateDto;
//import Project.OpenBook.Dto.error.ErrorDto;
//import Project.OpenBook.Dto.error.ErrorMsgDto;
//import Project.OpenBook.Repository.category.CategoryRepository;
//import Project.OpenBook.Chapter.Repo.ChapterRepository;
//import Project.OpenBook.Repository.chaptersection.ChapterSectionRepository;
//import Project.OpenBook.Topic.Repo.TopicRepository;
//import Project.OpenBook.Repository.choice.ChoiceRepository;
//import Project.OpenBook.Repository.topicprogress.TopicProgressRepository;
//import org.junit.jupiter.api.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.core.ParameterizedTypeReference;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
//import org.springframework.test.context.TestPropertySource;
//
//import java.util.*;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@TestPropertySource(properties = { "spring.config.location=classpath:application-test.yml" })
//class ChoiceControllerTest {
//
//    @LocalServerPort
//    protected int port;
//
//    @Autowired
//    TestRestTemplate restTemplate;
//
//    @Autowired
//    ChapterRepository chapterRepository;
//
//    @Autowired
//    CategoryRepository categoryRepository;
//
//    @Autowired
//    TopicRepository topicRepository;
//
//    @Autowired
//    ChoiceRepository choiceRepository;
//
//    @Autowired
//    ChapterSectionRepository chapterSectionRepository;
//
//    @Autowired
//    TopicProgressRepository topicProgressRepository;
//
//    private final String prefix = "http://localhost:";
//
//    private String URL,suffix;
//
//    Topic topic1, topic2;
//
//    Choice choice1, choice2, choice3, choice4, choice5;
//
//    private void initConfig(){
//        URL = prefix + port + suffix;
//        restTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
//    }
//
//    private void baseSetting(){
//        Chapter ch1 = new Chapter("ch1", 1);
//        Category c1 = new Category("c1");
//        chapterRepository.save(ch1);
//        categoryRepository.save(c1);
//
//        topic1 = new Topic("title1", null, null, false,false,0, 0, "detail1", ch1, c1);
//        topic2 = new Topic("title2", null, null, false,false,0, 0, "detail2", ch1, c1);
//
//        topicRepository.save(topic1);
//        topicRepository.save(topic2);
//
//        /**
//         * choice1,2,3 -> topic1
//         * choice4,5 -> topic2
//         */
////        choice1 = new Choice("choice1", topic1);
////        choice2 = new Choice("choice2", topic1);
////        choice3 = new Choice("choice3", topic1);
////
////        choice4 = new Choice("choice4", topic2);
////        choice5 = new Choice("choice5", topic2);
//
//        choiceRepository.saveAllAndFlush(Arrays.asList(choice1, choice2, choice3, choice4, choice5));
//    }
//    private void baseClear() {
//        choiceRepository.deleteAllInBatch();
//        topicRepository.deleteAllInBatch();
//        chapterRepository.deleteAllInBatch();
//        categoryRepository.deleteAllInBatch();
//    }
//
//
//
//    @Nested
//    @DisplayName("특정 선지 조회 - GET /admin/choices/{choiceId}")
//    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
//    public class queryChoice{
//        @BeforeAll
//        public void init(){
//            suffix = "/admin/choices/";
//            initConfig();
//        }
//
//        @BeforeEach
//        public void setting(){
//            baseSetting();
//        }
//
//        @AfterEach
//        public void clear(){
//            baseClear();
//        }
//
//        @DisplayName("특정 선지 조회 성공")
//        @Test
//        public void queryChoicesSuccess() {
//            Long choiceId = choice1.getId();
//
//            ResponseEntity<ChoiceDto> response = restTemplate.getForEntity(URL + choiceId, ChoiceDto.class);
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(new ChoiceDto(choice1));
//        }
//
//        @DisplayName("특정 선지 조회 실패 - 존재하지 않는 선지 id 입력")
//        @Test
//        public void getChoicesFail(){
//            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL + "-1", HttpMethod.GET,
//                    null, new ParameterizedTypeReference<List<ErrorMsgDto>>() {});
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto("존재하지 않는 선지 ID입니다.")));
//        }
//    }
//
//    @Nested
//    @DisplayName("선지 여러개 추가 - POST /admin/choices/")
//    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
//    public class createChoices{
//        @BeforeAll
//        public void init(){
//            suffix = "/admin/choices/";
//            initConfig();
//        }
//
//        @BeforeEach
//        public void setting(){
//            baseSetting();
//        }
//
//        @AfterEach
//        public void clear(){
//            baseClear();
//        }
//
//        @DisplayName("여러개의 선지 추가 성공")
//        @Test
//        public void createChoicesSuccess() {
//            String[] choiceArr = {"nc1", "nc2", "nc3"};
//
//            ChoiceAddDto choiceAddDto = new ChoiceAddDto("title1", choiceArr);
//
//            ResponseEntity<Void> response = restTemplate.postForEntity(URL, choiceAddDto, Void.class);
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
//
//            List<Choice> choiceList = choiceRepository.queryChoiceByTopicTitle("title1");
//            assertThat(choiceList.size()).isEqualTo(6); // 기존 3개 + 추가한 3개
//        }
//
//        @DisplayName("여러개의 선지 추가 실패 - DTO Validation")
//        @Test
//        public void createChoiceFailWrongDto(){
//            ChoiceAddDto choiceAddDto = new ChoiceAddDto();
//
//            ResponseEntity<List<ErrorDto>> response = restTemplate.exchange(URL, HttpMethod.POST, new HttpEntity<>(choiceAddDto), new ParameterizedTypeReference<List<ErrorDto>>() {
//            });
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
//            assertThat(response.getBody().size()).isEqualTo(2);
//        }
//
//        @DisplayName("여러개의 선지 추가 실패 - 존재하지 않는 토픽 제목 입력")
//        @Test
//        public void createChoiceFailNotExistTopic(){
//            String[] choiceArr = {"nc1", "nc2", "nc3"};
//            ChoiceAddDto choiceAddDto = new ChoiceAddDto("title-1", choiceArr);
//
//            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL, HttpMethod.POST,
//                    new HttpEntity<>(choiceAddDto), new ParameterizedTypeReference<List<ErrorMsgDto>>() {});
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto("존재하지 않는 토픽 제목입니다.")));
//        }
//
//        @DisplayName("여러개의 선지 추가 - 중복되는 내용의 선지 입력")
//        @Test
//        public void createChoiceFailDupContent() {
//            //3개중 1개가 내용이 중복됨
//            String[] choiceArr = {"choice1", "nc1", "nc2"};
//            ChoiceAddDto choiceAddDto = new ChoiceAddDto("title1", choiceArr);
//
//            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL, HttpMethod.POST,
//                    new HttpEntity<>(choiceAddDto), new ParameterizedTypeReference<List<ErrorMsgDto>>() {});
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
//            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto("중복된 선지내용입니다.")));
//
//        }
//    }
//
//    @Nested
//    @DisplayName("선지 수정 - PATCH /admin/choices/{choiceId}")
//    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
//    public class updateChoice{
//
//        private Long choiceId;
//        @BeforeAll
//        public void init(){
//            suffix = "/admin/choices/";
//            initConfig();
//        }
//
//        @BeforeEach
//        public void setting(){
//            baseSetting();
//            choiceId = choice1.getId();
//        }
//
//        @AfterEach
//        public void clear(){
//            baseClear();
//        }
//
//        @DisplayName("선지 수정 성공")
//        @Test
//        public void updateChoiceSuccess() {
//            ChoiceUpdateDto choiceUpdateDto = new ChoiceUpdateDto("nc1");
//
//            ResponseEntity<Void> response = restTemplate.exchange(URL + choiceId, HttpMethod.PATCH, new HttpEntity<>(choiceUpdateDto), Void.class);
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//
//            List<Choice> choiceList = choiceRepository.queryChoiceByTopicTitle("title1");
//            assertThat(choiceList.size()).isEqualTo(3); // 기존 3개
//
//            Choice afterChoice = choiceRepository.findById(choiceId).get();
//            assertThat(afterChoice.getContent()).isEqualTo("nc1");
//        }
//
//        @DisplayName("선지 수정 실패 - DTO Validation")
//        @Test
//        public void updateChoiceFailWrongDto(){
//            ChoiceUpdateDto choiceUpdateDto = new ChoiceUpdateDto();
//            ResponseEntity<List<ErrorDto>> response = restTemplate.exchange(URL + choiceId, HttpMethod.PATCH, new HttpEntity<>(choiceUpdateDto), new ParameterizedTypeReference<List<ErrorDto>>() {
//            });
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
//            assertThat(response.getBody().size()).isEqualTo(1);
//        }
//
//        @DisplayName("선지 수정 실패 - 중복되는 내용의 선지 내용 입력")
//        @Test
//        public void updateChoiceFailDupContent(){
//            ChoiceUpdateDto choiceUpdateDto = new ChoiceUpdateDto("choice3");
//
//            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL + choiceId, HttpMethod.PATCH,
//                    new HttpEntity<>(choiceUpdateDto), new ParameterizedTypeReference<List<ErrorMsgDto>>() {});
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
//            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto("중복된 선지내용입니다.")));
//        }
//
//        @DisplayName("선지 수정 실패 - 존재하지 않는 선지id 입력")
//        @Test
//        public void updateChoiceFailNotExistChoice(){
//            ChoiceUpdateDto choiceUpdateDto = new ChoiceUpdateDto("nc1");
//
//            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL + "123123", HttpMethod.PATCH,
//                    new HttpEntity<>(choiceUpdateDto), new ParameterizedTypeReference<List<ErrorMsgDto>>() {});
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto("존재하지 않는 선지 ID입니다.")));
//        }
//    }
//
//    @Nested
//    @DisplayName("선지 삭제 - DELETE /admin/choices/{choiceId}")
//    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
//    public class deleteChoice{
//
//        private Long choiceId;
//        @BeforeAll
//        public void init(){
//            suffix = "/admin/choices/";
//            initConfig();
//        }
//
//        @BeforeEach
//        public void setting(){
//            baseSetting();
//            choiceId = choice1.getId();
//        }
//
//        @AfterEach
//        public void clear(){
//            baseClear();
//        }
//
//        @DisplayName("선지 삭제 성공")
//        @Test
//        public void deleteChoiceSuccess() {
//            ResponseEntity<Void> response = restTemplate.exchange(URL + choiceId, HttpMethod.DELETE, null, Void.class);
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//
//            List<Choice> choiceList = choiceRepository.queryChoiceByTopicTitle("title1");
//            assertThat(choiceList.size()).isEqualTo(2); // 기존 3개
//            assertThat(choiceRepository.findById(choiceId).isEmpty()).isTrue();
//        }
//
//
//        @DisplayName("선지 삭제 실패 - 존재하지 않는 선지id 입력")
//        @Test
//        public void deleteChoiceFailNotExistChoice(){
//            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL + "123123", HttpMethod.DELETE,
//                    null, new ParameterizedTypeReference<List<ErrorMsgDto>>() {});
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto("존재하지 않는 선지 ID입니다.")));
//        }
//    }
//
//}