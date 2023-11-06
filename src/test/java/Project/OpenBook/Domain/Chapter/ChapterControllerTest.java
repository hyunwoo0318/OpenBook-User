//package Project.OpenBook.Domain.Chapter;
//
//
//import Project.OpenBook.Constants.ChoiceType;
//import Project.OpenBook.Domain.Category.Domain.Category;
//import Project.OpenBook.Domain.Chapter.Domain.Chapter;
//import Project.OpenBook.Domain.Chapter.Service.dto.*;
//import Project.OpenBook.Domain.StudyHistory.ChapterProgress.Domain.ChapterProgress;
//import Project.OpenBook.Domain.StudyHistory.ChapterSection.Domain.ChapterSection;
//import Project.OpenBook.Domain.Choice.Domain.Choice;
//import Project.OpenBook.Constants.Role;
//import Project.OpenBook.Domain.Customer.Domain.Customer;
//import Project.OpenBook.Domain.Description.Domain.Description;
//import Project.OpenBook.Handler.Exception.error.ErrorMsgDto;
//import Project.OpenBook.Domain.StudyHistory.Dto.ProgressDto;
//import Project.OpenBook.Domain.ExamQuestion.Domain.ExamQuestion;
//import Project.OpenBook.Domain.ExamQuestion.Repo.ExamQuestionRepository;
//import Project.OpenBook.Jwt.TokenManager;
//import Project.OpenBook.Domain.Keyword.Domain.Keyword;
//import Project.OpenBook.Domain.Category.Repository.CategoryRepository;
//import Project.OpenBook.Domain.Chapter.Repo.ChapterRepository;
//import Project.OpenBook.Domain.StudyHistory.ChapterProgress.Repository.ChapterProgressRepository;
//import Project.OpenBook.Domain.StudyHistory.ChapterSection.Repository.ChapterSectionRepository;
//import Project.OpenBook.Domain.Choice.Repository.ChoiceRepository;
//import Project.OpenBook.Domain.Customer.Repository.CustomerRepository;
//import Project.OpenBook.Domain.Keyword.Repository.KeywordRepository;
//import Project.OpenBook.Domain.Topic.Repo.TopicRepository;
//import Project.OpenBook.Domain.StudyHistory.TopicProgress.Repository.TopicProgressRepository;
//import Project.OpenBook.Domain.Round.Domain.Round;
//import Project.OpenBook.Domain.Round.Repo.RoundRepository;
//import Project.OpenBook.Domain.Customer.Service.CustomerService;
//import Project.OpenBook.Domain.Topic.Domain.Topic;
//import org.junit.jupiter.api.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.core.ParameterizedTypeReference;
//
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.TestPropertySource;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//import static Project.OpenBook.Constants.ContentConst.*;
//import static Project.OpenBook.Constants.ErrorCode.*;
//import static Project.OpenBook.Constants.JJHListStateConst.*;
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@TestPropertySource(properties = { "spring.config.location=classpath:application-test.yml" })
//@ContextConfiguration
//class ChapterControllerTest {
//
//    @LocalServerPort
//    int port;
//    @Autowired
//    ChapterRepository chapterRepository;
//    @Autowired
//    ChapterSectionRepository chapterSectionRepository;
//    @Autowired
//    ChapterProgressRepository chapterProgressRepository;
//
//    @Autowired
//    TopicProgressRepository topicProgressRepository;
//    @Autowired
//    CategoryRepository categoryRepository;
//    @Autowired
//    TopicRepository topicRepository;
//    @Autowired
//    KeywordRepository keywordRepository;
//
//    @Autowired
//    ChoiceRepository choiceRepository;
//
//    @Autowired
//    DescriptionRepository descriptionRepository;
//
//    @Autowired
//    CustomerRepository customerRepository;
//
//    @Autowired
//    ExamQuestionRepository examQuestionRepository;
//    @Autowired
//    RoundRepository roundRepository;
//    @Autowired
//    TokenManager tokenManager;
//    @Autowired
//    TestRestTemplate restTemplate;
//    @Autowired
//    CustomerService customerService;
//    @Autowired
//    PasswordEncoder passwordEncoder;
//
//    private Category c1;
//    private Chapter ch1;
//    private Topic t1;
//
//    private Round r1;
//    private ExamQuestion eq;
//    private Customer customer1, customer2;
//
//    private final int chapterNum = 111;
//    private final String prefix = "http://localhost:";
//    private String suffix;
//
//    String URL;
//
//    String chapterInfo;
//
//    private void initConfig() {
//        URL = prefix + port + suffix;
//    }
//
//    private void baseSetting() {
//
//        customer1 = new Customer("customer1", passwordEncoder.encode("customer1"), Role.USER);
//        customer2 = new Customer("customer2", passwordEncoder.encode("customer2"), Role.USER);
//        customerRepository.saveAllAndFlush(Arrays.asList(customer1, customer2));
//
//        c1 = new Category("유물");
//        categoryRepository.saveAndFlush(c1);
//
//        chapterInfo = "chapterInfo!!";
//        ch1 = new Chapter("ch1", chapterNum);
//        ch1.updateContent(chapterInfo);
//        chapterRepository.saveAndFlush(ch1);
//
//        t1 = new Topic("title1", 1234, 2314, false,false,0, 0, "detail1", ch1, c1);
//        topicRepository.saveAndFlush(t1);
//    }
//
//    private void baseClear() {
//        chapterProgressRepository.deleteAllInBatch();
//        chapterSectionRepository.deleteAllInBatch();
//        topicProgressRepository.deleteAllInBatch();
//        customerRepository.deleteAllInBatch();
//        topicRepository.deleteAllInBatch();
//        chapterRepository.deleteAllInBatch();
//        categoryRepository.deleteAllInBatch();
//    }
//
//
//
//    @Nested
//    @DisplayName("모든 단원 조회(관리자) - GET /admin/chapters")
//    @TestInstance(PER_CLASS)
//    public class queryChaptersAdmin{
//
//        @BeforeAll
//        public void init(){
//            suffix = "/admin/chapters";
//            initConfig();
//        }
//
//        @AfterEach
//        public void clear(){
//            baseClear();
//        }
//
//        @BeforeEach
//        public void setting() {
//            baseSetting();
//        }
//
//
//        @DisplayName("단원 전체 조회 성공")
//        @Test
//        public void queryChaptersSuccess() {
//            List<Chapter> chapterList = new ArrayList<>();
//            chapterList.add(ch1);
//
//            for (int i = 2; i <= 5; i++) {
//                Chapter c = new Chapter("title" + i, i);
//                chapterList.add(c);
//            }
//            chapterRepository.saveAllAndFlush(chapterList);
//
//
//            ResponseEntity<List<ChapterQueryAdminDto>> response = restTemplate
//                    .withBasicAuth("customer1", "customer1")
//                    .exchange(URL, HttpMethod.GET, null,
//                    new ParameterizedTypeReference<List<ChapterQueryAdminDto>>() {
//            });
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//            List<ChapterQueryAdminDto> expectResult = chapterList.stream()
//                    .map(c -> new ChapterQueryAdminDto(c.getTitle(), c.getNumber()))
//                    .collect(Collectors.toList());
//            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(expectResult);
//        }
//    }
//
//    @Nested
//    @DisplayName("모든 단원 조회(jjh) - GET /jjh/chapters")
//    @TestInstance(PER_CLASS)
//    public class queryChaptersJJH{
//
//        Chapter ch2, ch3, ch4;
//        @BeforeAll
//        public void init(){
//            suffix = "/jjh/chapters";
//            initConfig();
//        }
//
//        @AfterEach
//        public void clear(){
//            baseClear();
//        }
//
//        @BeforeEach
//        public void setting() {
//            baseSetting();
//            ch2 = new Chapter("ch2", 2);
//            ch3 = new Chapter("ch3", 3);
//            chapterRepository.saveAll(Arrays.asList(ch2, ch3));
//
//            /**
//             * ch2 -> 완료
//             * ch3 -> 연표 문제
//             * ch4 -> Not Started
//             */
//            ChapterProgress cp1 = new ChapterProgress(customer1, ch1, 0, COMPLETE.getName());
//            ChapterProgress cp2 = new ChapterProgress(customer1, ch2, 0, TIMELINE_STUDY.getName());
//            ChapterProgress cp3 = new ChapterProgress(customer1, ch3, 0, NOT_STARTED.getName());
//            chapterProgressRepository.saveAll(Arrays.asList(cp1, cp2, cp3));
//        }
//
//
//        @DisplayName("단원 전체 조회(사용자) 성공")
//        @Test
//        public void queryChaptersCustomerSuccess() {
//
//            ResponseEntity<List<ChapterUserDto>> response = restTemplate
//                    .withBasicAuth("customer1", "customer1")
//                    .exchange(URL, HttpMethod.GET, null, new ParameterizedTypeReference<List<ChapterUserDto>>() {});
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//            List<ChapterUserDto> body = response.getBody();
//            assertThat(body.size()).isEqualTo(chapterRepository.findAll().size());
//
//
//
//            //2단원
//            ChapterUserDto chapter2Dto
//                    = new ChapterUserDto(ch2.getTitle(), ch2.getNumber(),  OPEN.getName(), TIMELINE_STUDY.getName() );
//            assertThat(body.get(0)).usingRecursiveComparison().isEqualTo(chapter2Dto);
//
//            //3딘원
//            ChapterUserDto chapter3Dto
//                    = new ChapterUserDto(ch3.getTitle(), ch3.getNumber(), LOCKED.getName(),  NOT_STARTED.getName() );
//            assertThat(body.get(1)).usingRecursiveComparison().isEqualTo(chapter3Dto);
//
//            //111단원
//            ChapterUserDto chapter1Dto
//                    = new ChapterUserDto(ch1.getTitle(), ch1.getNumber(), OPEN.getName(), COMPLETE.getName() );
//            assertThat(body.get(2)).usingRecursiveComparison().isEqualTo(chapter1Dto);
//
//
//        }
//    }
//
//    @Nested
//    @DisplayName("모든 단원 조회(학습 자료 모음) - GET /jjh/chapters")
//    @TestInstance(PER_CLASS)
//    public class queryChaptersCustomer{
//
//        Chapter ch2, ch3, ch4;
//        @BeforeAll
//        public void init(){
//            suffix = "/chapters";
//            initConfig();
//        }
//
//        @AfterEach
//        public void clear(){
//            baseClear();
//        }
//
//        @BeforeEach
//        public void setting() {
//            baseSetting();
//            ch2 = new Chapter("ch2", 2);
//            ch3 = new Chapter("ch3", 3);
//            ch4 = new Chapter("ch4", 4);
//            chapterRepository.saveAll(Arrays.asList(ch2, ch3, ch4));
//
//            /**
//             * ch별 topic개수
//             * ch1 -> 1개
//             * ch2 -> 4개
//             * ch3 -> 3개
//             * ch4 -> 3개
//             */
//            List<Topic> topicList = new ArrayList<>();
//            for (int i = 0; i < 10; i++) {
//                if (i < 4) {
//                    Topic topic = new Topic("topic" + i, ch2);
//                    topicList.add(topic);
//                } else if (i < 7) {
//                    Topic topic = new Topic("topic" + i, ch3);
//                    topicList.add(topic);
//                }else{
//                    Topic topic = new Topic("topic" + i, ch4);
//                    topicList.add(topic);
//                }
//
//            }
//            topicRepository.saveAllAndFlush(topicList);
//        }
//
//
//        @DisplayName("단원 전체 조회(학습 자료 모음) 성공")
//        @Test
//        public void queryChaptersCustomerSuccess() {
//
//            ResponseEntity<List<ChapterDetailDto>> response = restTemplate
//                    .withBasicAuth("customer1", "customer1")
//                    .exchange(URL, HttpMethod.GET, null, new ParameterizedTypeReference<List<ChapterDetailDto>>() {});
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//            List<ChapterDetailDto> body = response.getBody();
//            assertThat(body.size()).isEqualTo(chapterRepository.findAll().size());
//
//            ChapterDetailDto dto1 = new ChapterDetailDto(ch1.getTitle(), ch1.getNumber(), ch1.getStartDate(), ch1.getEndDate(), 1);
//            ChapterDetailDto dto2 = new ChapterDetailDto(ch2.getTitle(), ch2.getNumber(), ch2.getStartDate(), ch2.getEndDate(), 4);
//            ChapterDetailDto dto3 = new ChapterDetailDto(ch3.getTitle(), ch3.getNumber(), ch3.getStartDate(), ch3.getEndDate(), 3);
//            ChapterDetailDto dto4 = new ChapterDetailDto(ch4.getTitle(), ch4.getNumber(), ch4.getStartDate(), ch4.getEndDate(), 3);
//
//            assertThat(body).usingRecursiveComparison().isEqualTo(Arrays.asList(dto1, dto2, dto3, dto4));
//        }
//    }
//
//    @Nested
//    @DisplayName("목차 조회 - GET /contents-table")
//    @TestInstance(PER_CLASS)
//    public class queryContentsTable{
//
//        private ChapterSection cs1,cs2,cs3,cs4;
//
//        @BeforeAll
//        public void init(){
//            suffix = "/contents-table";
//            initConfig();
//        }
//
//        @AfterEach
//        public void clear(){
//            baseClear();
//        }
//
//        @BeforeEach
//        public void setting() {
//            baseSetting();
//
//            cs1 = new ChapterSection(customer1, ch1, CHAPTER_INFO.getName(), OPEN.getName());
//            cs2 = new ChapterSection(customer1, ch1, TIMELINE_STUDY.getName(), OPEN.getName());
//            cs4 = new ChapterSection(customer1, ch1, CHAPTER_COMPLETE_QUESTION.getName(), LOCKED.getName());
//
//            chapterSectionRepository.saveAllAndFlush(Arrays.asList(cs1, cs2, cs4));
//        }
//
//
//        @DisplayName("목차 조회 성공")
//        @Test
//        public void queryContentsTableSuccess() {
//
//            ResponseEntity<List<ProgressDto>> response = restTemplate
//                    .withBasicAuth("customer1", "customer1")
//                    .exchange(URL + "?num=" + chapterNum, HttpMethod.GET, null, new ParameterizedTypeReference<List<ProgressDto>>() {});
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//            List<ProgressDto> body = response.getBody();
//            assertThat(body.size()).isEqualTo(4);
//
//
//            ProgressDto dto1 = new ProgressDto(cs1.getContent(), ch1.getTitle(), cs1.getState());
//            ProgressDto dto2 = new ProgressDto(cs2.getContent(),  ch1.getTitle(), cs2.getState());
//            ProgressDto dto3 = new ProgressDto(TOPIC_STUDY.getName(), t1.getTitle(), LOCKED.getName());
//            ProgressDto dto4 = new ProgressDto(cs4.getContent(),  ch1.getTitle(), cs4.getState());
//
//            assertThat(body).usingRecursiveComparison().isEqualTo(Arrays.asList(dto1, dto2, dto3, dto4));
//        }
//
//        @DisplayName("목차 조회 실패 - 존재하지 않는 단원번호 입력")
//        @Test
//        public void queryContentsTableFail() {
//            ResponseEntity<List<ErrorMsgDto>> response = restTemplate
//                    .withBasicAuth("customer1", "customer1")
//                    .exchange(URL + "?num=-1", HttpMethod.GET,
//                    null, new ParameterizedTypeReference<List<ErrorMsgDto>>() {});
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto(CHAPTER_NOT_FOUND.getErrorMessage())));
//        }
//    }
//
//
//    @Nested
//    @DisplayName("단원 이름 조회 - GET /chapters/chapter-title")
//    @TestInstance(PER_CLASS)
//    public class queryChapterTitle{
//
//        @BeforeAll
//        public void init(){
//            suffix = "/chapters/chapter-title";
//            initConfig();
//        }
//
//        @AfterEach
//        public void clear(){
//            baseClear();
//        }
//
//        @BeforeEach
//        public void setting() {
//            baseSetting();
//        }
//
//
//        @DisplayName("단원 이름 조회 성공")
//        @Test
//        public void queryChaptersSuccess() {
//            ResponseEntity<ChapterTitleDto> response = restTemplate
//                    .withBasicAuth("customer1", "customer1")
//                    .getForEntity(URL + "?num=" + chapterNum, ChapterTitleDto.class);
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//            assertThat(response.getBody().getTitle()).isEqualTo("ch1");
//
//        }
//
//        @DisplayName("단원 이름 조회 실패 - 존재하지 않는 단원번호 입력")
//        @Test
//        public void queryChaptersFail() {
//            ResponseEntity<List<ErrorMsgDto>> response = restTemplate
//                    .withBasicAuth("customer1", "customer1")
//                    .exchange(URL + "?num=-1", HttpMethod.GET,
//                    null, new ParameterizedTypeReference<List<ErrorMsgDto>>() {});
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto(CHAPTER_NOT_FOUND.getErrorMessage())));
//        }
//    }
//
//
//
//    @Nested
//    @DisplayName("단원 학습 조회 - GET /chapters/{num}/info")
//    @TestInstance(PER_CLASS)
//    public class queryChapterInfoAdmin{
//
//        @BeforeAll
//        public void init(){
//            suffix = "/chapters/";
//            initConfig();
//        }
//
//        @AfterEach
//        public void clear(){
//            baseClear();
//        }
//
//        @BeforeEach
//        public void setting() {
//            baseSetting();
//        }
//
//
//        @DisplayName("단원 학습 조회 성공")
//        @Test
//        public void queryChapterInfoSuccess() {
//            ResponseEntity<ChapterInfoDto> response = restTemplate
//                    .withBasicAuth("customer1", "customer1")
//                    .getForEntity(URL + ch1.getNumber() + "/info", ChapterInfoDto.class);
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//            assertThat(response.getBody().getContent()).isEqualTo(ch1.getContent());
//        }
//
//        @DisplayName("단원 학습 조회 실패 - 존재하지 않는 단원번호 입력")
//        @Test
//        public void queryChapterInfoFail() {
//            ResponseEntity<List<ErrorMsgDto>> response = restTemplate
//                    .withBasicAuth("customer1", "customer1")
//                    .exchange(URL + "-111/info", HttpMethod.GET,
//                    null, new ParameterizedTypeReference<List<ErrorMsgDto>>() {});
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto(CHAPTER_NOT_FOUND.getErrorMessage())));
//
//        }
//    }
//
//    @Nested
//    @DisplayName("단원 시작년도/종료년도 조회 - GET /chapters/{num}/date")
//    @TestInstance(PER_CLASS)
//    public class queryChapterDate{
//
//        @BeforeAll
//        public void init(){
//            suffix = "/chapters/";
//            initConfig();
//        }
//
//        @AfterEach
//        public void clear(){
//            baseClear();
//        }
//
//        @BeforeEach
//        public void setting() {
//            baseSetting();
//        }
//
//
//        @DisplayName("시작년도/종료년도 조회 성공")
//        @Test
//        public void queryChapterDateSuccess() {
//            ResponseEntity<ChapterDateDto> response = restTemplate
//                    .withBasicAuth("customer1", "customer1")
//                    .getForEntity(URL + ch1.getNumber() + "/date", ChapterDateDto.class);
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//            assertThat(response.getBody()).usingRecursiveComparison()
//                    .isEqualTo(new ChapterDateDto(ch1.getStartDate(), ch1.getEndDate()));
//        }
//
//        @DisplayName("시작년도/종료년도 조회 실패 - 존재하지 않는 단원번호 입력")
//        @Test
//        public void queryChapterDateFail() {
//            ResponseEntity<List<ErrorMsgDto>> response = restTemplate
//                    .withBasicAuth("customer1", "customer1")
//                    .exchange(URL + "-111/date", HttpMethod.GET,
//                    null, new ParameterizedTypeReference<List<ErrorMsgDto>>() {});
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto(CHAPTER_NOT_FOUND.getErrorMessage())));
//
//        }
//    }
//
//
//    @Nested
//    @DisplayName("해당 단원의 모든 토픽 조회(관리자) - GET /admin/chapters/{num}/topics")
//    @TestInstance(PER_CLASS)
//    public class queryChapterTopicsAdmin{
//
//        @BeforeAll
//        public void init(){
//            suffix = "/admin/chapters/";
//            initConfig();
//        }
//
//        @AfterEach
//        public void clear(){
//            keywordRepository.deleteAllInBatch();
//            descriptionRepository.deleteAllInBatch();
//            choiceRepository.deleteAllInBatch();
//            examQuestionRepository.deleteAllInBatch();
//            roundRepository.deleteAllInBatch();
//            baseClear();
//        }
//
//        @BeforeEach
//        public void setting() {
//            baseSetting();
//
//            r1 = new Round(123, 1);
//            roundRepository.save(r1);
//
//            eq = new ExamQuestion(r1, 10, 2, ChoiceType.String);
//            examQuestionRepository.save(eq);
//
//            //t1에 키워드, 선지, 보기를 각각 2개씩 추가
//            Keyword k1 = new Keyword("k1","c1", t1,null);
//            Keyword k2 = new Keyword("k2","c2",t1,null);
//            keywordRepository.save(k1);
//            keywordRepository.save(k2);
//
//            Choice choice1 = new Choice(ChoiceType.String,"choice1", "comment1", t1,eq );
//            Choice choice2 = new Choice(ChoiceType.String,"choice2", "comment2", t1, eq);
//            choiceRepository.save(choice1);
//            choiceRepository.save(choice2);
//
//            Description des1 = new Description("des1", "comment2", t1, eq);
//            Description des2 = new Description("des2", "comment2", t1, eq);
//            descriptionRepository.save(des1);
//            descriptionRepository.save(des2);
//        }
//
//
//        @DisplayName("해당 단원의 모든 토픽 조회 성공")
//        @Test
//        public void queryChapterTopicsAdminSuccess() {
//            ResponseEntity<List<ChapterTopicWithCountDto>> response = restTemplate
//                    .withBasicAuth("customer1", "customer1")
//                    .exchange(URL + chapterNum + "/topics", HttpMethod.GET, null,
//                    new ParameterizedTypeReference<List<ChapterTopicWithCountDto>>() {
//            });
//            List<ChapterTopicWithCountDto> body = response.getBody();
//            ChapterTopicWithCountDto expectBody = new ChapterTopicWithCountDto("유물", 0,"title1", 1234, 2314, 2, 2, 2);
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//            assertThat(body).usingRecursiveComparison().isEqualTo(Arrays.asList(expectBody));
//        }
//
//        @DisplayName("해당 단원의 모든 토픽 조회 실패 - 존재하지 않는 단원번호 입력")
//        @Test
//        public void queryChapterTopicsAdminFail() {
//            ResponseEntity<List<ErrorMsgDto>> response = restTemplate
//                    .withBasicAuth("customer1", "customer1")
//                    .exchange(URL + "-1/topics", HttpMethod.GET,
//                    null, new ParameterizedTypeReference<List<ErrorMsgDto>>() {});
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto(CHAPTER_NOT_FOUND.getErrorMessage())));
//
//        }
//
//    }
//
//    @Nested
//    @DisplayName("해당 단원의 모든 토픽 조회(사용자) - GET /chapters/{num}/topics")
//    @TestInstance(PER_CLASS)
//    public class queryChapterTopicsCustomer{
//
//        private Topic t2;
//        @BeforeAll
//        public void init(){
//            suffix = "/chapters/";
//            initConfig();
//        }
//
//        @AfterEach
//        public void clear(){
//            baseClear();
//        }
//
//        @BeforeEach
//        public void setting() {
//            baseSetting();
//
//            t2 = new Topic("title2", 123, 456, false, false, 0, 0, "detail2", ch1, c1);
//            topicRepository.save(t2);
//        }
//
//
//        @DisplayName("해당 단원의 모든 토픽 조회 성공")
//        @Test
//        public void queryChapterTopicsCustomerSuccess() {
//            ResponseEntity<List<ChapterTopicCustomerDto>> response = restTemplate
//                    .withBasicAuth("customer1", "customer1")
//                    .exchange(URL + chapterNum + "/topics", HttpMethod.GET, null,
//                    new ParameterizedTypeReference<List<ChapterTopicCustomerDto>>() {
//                    });
//            List<ChapterTopicCustomerDto> body = response.getBody();
//            ChapterTopicCustomerDto dto1 = new ChapterTopicCustomerDto(t1);
//            ChapterTopicCustomerDto dto2 = new ChapterTopicCustomerDto(t2);
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//            assertThat(body).usingRecursiveComparison().isEqualTo(Arrays.asList(dto1, dto2));
//        }
//
//        @DisplayName("해당 단원의 모든 토픽 조회 실패 - 존재하지 않는 단원번호 입력")
//        @Test
//        public void queryChapterTopicsCustomerFail() {
//            ResponseEntity<List<ErrorMsgDto>> response = restTemplate
//                    .withBasicAuth("customer1", "customer1")
//                    .exchange(URL + "-1/topics", HttpMethod.GET,
//                    null, new ParameterizedTypeReference<List<ErrorMsgDto>>() {});
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto(CHAPTER_NOT_FOUND.getErrorMessage())));
//
//        }
//
//    }
//
//
//
//    @Nested
//    @DisplayName("단원 학습 수정- PATCH /admin/chapters/{num}/info")
//    @TestInstance(PER_CLASS)
//    public class updateChapterInfo{
//
//        private int chapterNum;
//        @BeforeAll
//        public void init(){
//            suffix = "/admin/chapters/";
//            initConfig();
//        }
//
//        @AfterEach
//        public void clear(){
//            baseClear();
//        }
//
//        @BeforeEach
//        public void setting() {
//            baseSetting();
//            chapterNum = ch1.getNumber();
//        }
//
//        @DisplayName("단원 학습 수정 성공")
//        @Test
//        public void updateChapterInfoSuccess() {
//            ChapterInfoDto dto = new ChapterInfoDto("new Info");
//            ResponseEntity<Void> response = restTemplate
//                    .withBasicAuth("customer1", "customer1")
//                    .exchange(URL + chapterNum + "/info", HttpMethod.PATCH,
//                    new HttpEntity<>(dto), Void.class);
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//            assertThat(chapterRepository.findOneByNumber(chapterNum).get().getContent()).isEqualTo(dto.getContent());
//        }
//
//
//        @DisplayName("단원 학습 수정 실패 - DTO Validation")
//        @Test
//        public void updateChapterInfoFailWrongDto() {
//            //내용을 입력하지 않은 경우
//            ChapterInfoDto wrongDto = new ChapterInfoDto();
//
//            ResponseEntity<List<ErrorMsgDto>> response = restTemplate
//                    .withBasicAuth("customer1", "customer1")
//                    .exchange(URL + chapterNum + "/info", HttpMethod.PATCH,
//                    new HttpEntity<>(wrongDto), new ParameterizedTypeReference<List<ErrorMsgDto>>() {});
//
//            List<ErrorMsgDto> body = response.getBody();
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
//            assertThat(body.size()).isEqualTo(1);
//        }
//
//        @DisplayName("단원 학습 수정 실패 - 존재하지 않는 단원 번호 입력")
//        @Test
//        public void updateChapterFailNotFoundNum(){
//            ChapterInfoDto dto = new ChapterInfoDto("new Content");
//            //존재하지 않는 단원 수정 요청
//            ResponseEntity<List<ErrorMsgDto>> response = restTemplate
//                    .withBasicAuth("customer1", "customer1")
//                    .exchange(URL + "-1/info", HttpMethod.PATCH,
//                    new HttpEntity<>(dto), new ParameterizedTypeReference<List<ErrorMsgDto>>() {});
//
//            List<ErrorMsgDto> body = response.getBody();
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//            assertThat(body).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto(CHAPTER_NOT_FOUND.getErrorMessage())));
//
//        }
//    }
//
//
//    @Nested
//    @DisplayName("단원 추가- POST /admin/chapters")
//    @TestInstance(PER_CLASS)
//    public class createChapter{
//
//        private int prevSize;
//        @BeforeAll
//        public void init(){
//            suffix = "/admin/chapters";
//            initConfig();
//        }
//
//        @AfterEach
//        public void clear(){
//            baseClear();
//        }
//
//        @BeforeEach
//        public void setting() {
//            baseSetting();
//            prevSize = chapterRepository.findAll().size();
//        }
//
//        @DisplayName("새로운 단원 저장 성공")
//        @Test
//        public void createNewChapterSuccess() {
//            ChapterAddUpdateDto dto = new ChapterAddUpdateDto("ch2", 2, 123, 456);
//            ResponseEntity<Void> response = restTemplate
//                    .withBasicAuth("customer1", "customer1")
//                    .postForEntity(URL, dto,Void.class);
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
//            Chapter chapter = chapterRepository.findOneByNumber(2).get();
//            assertThat(chapter.getTitle()).isEqualTo(dto.getTitle());
//            assertThat(chapter.getStartDate()).isEqualTo(dto.getStartDate());
//            assertThat(chapter.getEndDate()).isEqualTo(dto.getEndDate());
//            assertThat(chapterRepository.findAll().size()).isEqualTo(prevSize + 1);
//        }
//
//
//        @DisplayName("잘못된 입력으로 단원 저장 실패 - DTO Validation")
//        @Test
//        public void createChapterFailWrongDto() {
//            //제목, 번호를 입력하지 않은 경우
//            ChapterAddUpdateDto wrongDto = new ChapterAddUpdateDto();
//
//            ResponseEntity<List<ErrorMsgDto>> response = restTemplate
//                    .withBasicAuth("customer1", "customer1")
//                    .exchange(URL, HttpMethod.POST,
//                    new HttpEntity<>(wrongDto), new ParameterizedTypeReference<List<ErrorMsgDto>>() {});
//
//            List<ErrorMsgDto> body = response.getBody();
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
//            assertThat(body.size()).isEqualTo(2);
//
//            assertThat(chapterRepository.findAll().size()).isEqualTo(prevSize);
//
//        }
//
//        @DisplayName("잘못된 입력으로 단원 저장 실패 - 중복된 단원번호를 입력한 경우")
//        @Test
//        public void createChapterFailDupNum() {
//             //중복된 단원번호를 입력한 경우
//            ChapterAddUpdateDto wrongDto = new ChapterAddUpdateDto("title123", chapterNum, 123, 456);
//
//            ResponseEntity<List<ErrorMsgDto>> response = restTemplate
//                    .withBasicAuth("customer1", "customer1")
//                    .exchange(URL,HttpMethod.POST,
//                    new HttpEntity<>(wrongDto),new ParameterizedTypeReference<List<ErrorMsgDto>>(){});
//
//            List<ErrorMsgDto> body = response.getBody();
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
//            assertThat(body).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto(DUP_CHAPTER_NUM.getErrorMessage())));
//
//            assertThat(chapterRepository.findAll().size()).isEqualTo(prevSize);
//        }
//
//    }
//
//    @Nested
//    @DisplayName("단원 수정 - PATCH /admin/chapters")
//    @TestInstance(PER_CLASS)
//    public class updateChapter{
//
//        private int prevSize;
//        @BeforeAll
//        public void init(){
//            suffix = "/admin/chapters";
//            initConfig();
//        }
//
//        @AfterEach
//        public void clear(){
//            baseClear();
//        }
//
//        @BeforeEach
//        public void setting() {
//            baseSetting();
//            Chapter ch2 = new Chapter("ch2", 2);
//            chapterRepository.saveAndFlush(ch2);
//            prevSize = chapterRepository.findAll().size();
//        }
//
//        @DisplayName("단원 수정 성공")
//        @Test
//        public void updateChapterSuccess() {
//            ChapterAddUpdateDto inputDto = new ChapterAddUpdateDto("titleAfterUpdate", 3, 123,456);
//
//            ResponseEntity<Void> response = restTemplate
//                    .withBasicAuth("customer1", "customer1")
//                    .exchange(URL + "/" + chapterNum, HttpMethod.PATCH, new HttpEntity<>(inputDto), Void.class);
//
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//
//            assertThat(chapterRepository.findOneByNumber(1).isEmpty()).isTrue();
//            Chapter afterChapter = chapterRepository.findOneByNumber(3).get();
//            assertThat(afterChapter.getTitle()).isEqualTo(inputDto.getTitle());
//            assertThat(afterChapter.getStartDate()).isEqualTo(inputDto.getStartDate());
//            assertThat(afterChapter.getEndDate()).isEqualTo(inputDto.getEndDate());
//
//            assertThat(chapterRepository.findAll().size()).isEqualTo(prevSize);
//        }
//
//        @DisplayName("단원 수정 실패 - DTO Validation")
//        @Test
//        public void updateChapterFailWrongDto(){
//            //단원번호, 제목을 입력하지않음
//            ChapterAddUpdateDto wrongDto = new ChapterAddUpdateDto();
//
//            ResponseEntity<List<ErrorMsgDto>> response = restTemplate
//                    .withBasicAuth("customer1", "customer1")
//                    .exchange(URL + "/" + chapterNum, HttpMethod.PATCH,
//                    new HttpEntity<>(wrongDto), new ParameterizedTypeReference<List<ErrorMsgDto>>() {});
//
//            List<ErrorMsgDto> body = response.getBody();
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
//            assertThat(body.size()).isEqualTo(2);
//
//            assertThat(chapterRepository.findAll().size()).isEqualTo(prevSize);
//        }
//
//        @DisplayName("단원 수정 실패 - 존재하지 않는 단원 번호 입력")
//        @Test
//        public void updateChapterFailNotFoundNum(){
//            ChapterAddUpdateDto dto = new ChapterAddUpdateDto("title123", 5, 123,456);
//
//            //존재하지 않는 단원 수정 요청
//            ResponseEntity<List<ErrorMsgDto>> response = restTemplate
//                    .withBasicAuth("customer1", "customer1")
//                    .exchange(URL + "/-1", HttpMethod.PATCH,
//                    new HttpEntity<>(dto), new ParameterizedTypeReference<List<ErrorMsgDto>>() {});
//
//            List<ErrorMsgDto> body = response.getBody();
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//            assertThat(body).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto(CHAPTER_NOT_FOUND.getErrorMessage())));
//
//            assertThat(chapterRepository.findAll().size()).isEqualTo(prevSize);
//        }
//
//        @DisplayName("단원 수정 실패 - 이미 존재하는 단원 번호로 수정 요청")
//        @Test
//        public void updateChapterFailDupNum(){
//            ChapterAddUpdateDto dto = new ChapterAddUpdateDto("title123", 2, 132, 456);
//
//            //존재하지 않는 단원 수정 요청
//            ResponseEntity<List<ErrorMsgDto>> response = restTemplate
//                    .withBasicAuth("customer1", "customer1")
//                    .exchange(URL + "/" + chapterNum, HttpMethod.PATCH,
//                    new HttpEntity<>(dto), new ParameterizedTypeReference<List<ErrorMsgDto>>() {});
//
//            List<ErrorMsgDto> body = response.getBody();
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
//            assertThat(body).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto(DUP_CHAPTER_NUM.getErrorMessage())));
//
//            assertThat(chapterRepository.findAll().size()).isEqualTo(prevSize);
//        }
//    }
//
//
//    @Nested
//    @DisplayName("단원 삭제 - DELETE admin/chapters")
//    @TestInstance(PER_CLASS)
//    public class deleteChapter {
//
//        private int prevSize;
//        @BeforeAll
//        public void init(){
//            suffix = "/admin/chapters";
//            initConfig();
//        }
//
//        @AfterEach
//        public void clear(){
//            baseClear();
//        }
//
//        @BeforeEach
//        public void setting() {
//            baseSetting();
//            prevSize = chapterRepository.findAll().size();
//        }
//
//
//        @DisplayName("단원 삭제 성공")
//        @Test
//        public void deleteChapterSuccess() {
//            topicRepository.deleteAllInBatch();
//            ResponseEntity<Void> response = restTemplate
//                    .withBasicAuth("customer1", "customer1")
//                    .exchange(URL + "/"+ chapterNum, HttpMethod.DELETE, null, Void.class);
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//            assertThat(chapterRepository.findOneByNumber(chapterNum).isEmpty()).isTrue();
//
//            assertThat(chapterRepository.findAll().size()).isEqualTo(prevSize-1);
//        }
//
//        @DisplayName("단원 삭제 실패 - 존재하지 않는 단원번호 입력")
//        @Test
//        public void deleteChapterFailNotFoundNum() {
//            //존재하지 않는 단원번호 입력
//            ResponseEntity<List<ErrorMsgDto>> response = restTemplate
//                    .withBasicAuth("customer1", "customer1")
//                    .exchange(URL + "/-1", HttpMethod.DELETE,
//                    null, new ParameterizedTypeReference<List<ErrorMsgDto>>() {});
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto(CHAPTER_NOT_FOUND.getErrorMessage())));
//
//            assertThat(chapterRepository.findOneByNumber(chapterNum).isPresent()).isTrue();
//            assertThat(chapterRepository.findAll().size()).isEqualTo(prevSize);
//        }
//
//        @DisplayName("단원 삭제 실패 - 토픽이 존재하는 단원 삭제 시도")
//        @Test
//        public void deleteChapterFailHasTopic() {
//            //토픽이 존재하는 단원 삭제 시도
//            ResponseEntity<List<ErrorMsgDto>> response = restTemplate
//                    .withBasicAuth("customer1", "customer1")
//                    .exchange(URL + "/" + chapterNum, HttpMethod.DELETE,
//                    null, new ParameterizedTypeReference<List<ErrorMsgDto>>() {});
//
//           assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
//            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto(CHAPTER_HAS_TOPIC.getErrorMessage())));
//
//            assertThat(chapterRepository.findOneByNumber(chapterNum).isPresent()).isTrue();
//            assertThat(chapterRepository.findAll().size()).isEqualTo(prevSize);
//        }
//
//
//    }
//
//
//}