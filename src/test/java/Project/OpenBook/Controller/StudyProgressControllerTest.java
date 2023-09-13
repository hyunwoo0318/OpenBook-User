//package Project.OpenBook.Controller;
//
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.TestPropertySource;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@TestPropertySource(properties = { "spring.config.location=classpath:application-test.yml" })
//public class StudyProgressControllerTest {
////    @LocalServerPort
////    int port;
////    @Autowired
////    ChapterRepository chapterRepository;
////    @Autowired
////    TopicRepository topicRepository;
////    @Autowired
////    CategoryRepository categoryRepository;
////    @Autowired
////    TestRestTemplate restTemplate;
////    @Autowired
////    TopicProgressRepository topicProgressRepository;
////    @Autowired
////    ChapterSectionRepository chapterSectionRepository;
////    @Autowired
////    CustomerRepository customerRepository;
////    @Autowired
////    PasswordEncoder passwordEncoder;
////
////    private final String prefix = "http://localhost:";
////    private String suffix;
////    String URL;
////
////    private Chapter ch1, ch2;
////    private Topic t1,t2;
////    private Customer customer1, customer2;
////
////    private void initConfig() {
////        URL = prefix + port + suffix;
////        restTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
////    }
////
////    private void baseSetting(){
////        ch1 = new Chapter("c1", 1);
////        ch2 = new Chapter("c2",2);
////        chapterRepository.saveAllAndFlush(Arrays.asList(ch1, ch2));
////
////        Category category = new Category("c1");
////        categoryRepository.saveAndFlush(category);
////
////        customer1 = new Customer("customer1", passwordEncoder.encode("customer1"), Role.USER);
////        customer2 = new Customer("customer2", passwordEncoder.encode("customer2"), Role.USER);
////        customerRepository.saveAllAndFlush(Arrays.asList(customer1, customer2));
////
////        t1 = new Topic("title1", 100, 200, false, false, 0, 0, "detail1", ch1, category);
////        t2 = new Topic("title2", 300, 400, false, false, 0, 0, "detail2", ch1, category);
////        topicRepository.saveAllAndFlush(Arrays.asList(t1, t2));
////
////        ChapterSection chapterSection = new ChapterSection(customer1, ch1);
////        chapterSectionRepository.saveAndFlush(chapterSection);
////        chapterSection.updateWrongCount(3);
////        chapterSectionRepository.saveAndFlush(chapterSection);
////
////        TopicProgress topicProgress = new TopicProgress(customer1, t1);
////        topicProgressRepository.saveAndFlush(topicProgress);
////        topicProgress.updateWrongCount(3);
////        topicProgressRepository.saveAndFlush(topicProgress);
////    }
////
////    private void baseClear(){
////        topicProgressRepository.deleteAllInBatch();
////        chapterSectionRepository.deleteAllInBatch();
////        customerRepository.deleteAllInBatch();
////        topicRepository.deleteAllInBatch();
////        categoryRepository.deleteAllInBatch();
////        chapterRepository.deleteAllInBatch();
////    }
////
////    @Nested
////    @DisplayName("단원 학습 정보 입력(오답) - POST /study-progress/chapter/wrong-count")
////    @TestInstance(PER_CLASS)
////    public class addChapterSectionWrongCount {
////        @BeforeAll
////        public void init(){
////            suffix = "/study-progress/chapter/wrong-count";
////            initConfig();
////        }
////
////        @AfterEach
////        public void clear(){
////            baseClear();
////        }
////
////        @BeforeEach
////        public void setting() {
////            baseSetting();
////        }
////
////        @DisplayName("단원 학습 정보 입력(오답) 성공 - 처음 학습한 경우")
////        @Test
////        public void addChapterProgressSuccessFirst() {
////            ChapterProgressAddDto dto = new ChapterProgressAddDto(ch1.getNumber(), 5);
////
////            ResponseEntity<Void> response = restTemplate
////                    .withBasicAuth("customer1", "customer1")
////                    .postForEntity(URL, dto, Void.class);
////
////            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
////            Optional<ChapterSection> chapterProgressOptional = chapterSectionRepository.queryChapterProgress(customer1.getId(), ch1.getNumber());
////            assertThat(chapterProgressOptional.get().getWrongCount()).isEqualTo(8);
////        }
////
////        @DisplayName("단원 학습 정보 입력(오답) 성공 - 이전에 학습한적이 있는 경우")
////        @Test
////        public void addChapterProgressSuccessNotFirst() {
////            ChapterProgressAddDto dto = new ChapterProgressAddDto(ch1.getNumber(), 5);
////            ResponseEntity<Void> response = restTemplate
////                    .withBasicAuth("customer1", "customer1")
////                    .postForEntity(URL, dto, Void.class);
////
////            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
////            Optional<ChapterSection> chapterProgressOptional = chapterSectionRepository.queryChapterProgress(customer1.getId(), ch1.getNumber());
////            assertThat(chapterProgressOptional.get().getWrongCount()).isEqualTo(8); // 기존 3 + 추가된 5
////        }
////
////        @DisplayName("단원 학습 정보 입력 실패 - DTO Validation")
////        @Test
////        public void addChapterProgressFailWrongDto() {
////            //필수조건인 회원아이디, 단원번호 생략
////            ChapterProgressAddDto dto = new ChapterProgressAddDto();
////            ResponseEntity<List<ErrorDto>> response = restTemplate
////                    .withBasicAuth("customer1", "customer1")
////                    .exchange(URL, HttpMethod.POST, new HttpEntity<>(dto), new ParameterizedTypeReference<List<ErrorDto>>() {
////            });
////
////            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
////            assertThat(response.getBody().size()).isEqualTo(1);
////        }
////
////        @DisplayName("단원 학습 정보 입력 실패 - 존재하지 않는 단원 번호 입력")
////        @Test
////        public void addChapterProgressFailNotFoundChapterNum(){
////            ChapterProgressAddDto dto = new ChapterProgressAddDto(-1, 5);
////            ResponseEntity<List<ErrorMsgDto>> response = restTemplate
////                    .withBasicAuth("customer1", "customer1")
////                    .exchange(URL, HttpMethod.POST, new HttpEntity<>(dto), new ParameterizedTypeReference<List<ErrorMsgDto>>() {
////            });
////
////            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
////            assertThat(response.getBody().size()).isEqualTo(1);
////            assertThat(response.getBody().get(0)).usingRecursiveComparison().isEqualTo(new ErrorMsgDto(ErrorCode.CHAPTER_NOT_FOUND.getErrorMessage()));
////        }
////    }
////
////    @Nested
////    @DisplayName("단원 학습 정보 입력(progress 갱신) - POST /study-progress/chapter/progress")
////    @TestInstance(PER_CLASS)
////    public class addChapterProgressSectionUpdate {
////        @BeforeAll
////        public void init(){
////            suffix = "/study-progress/chapter/progress";
////            initConfig();
////        }
////
////        @AfterEach
////        public void clear(){
////            baseClear();
////        }
////
////        @BeforeEach
////        public void setting() {
////            baseSetting();
////        }
////
////        @DisplayName("단원 학습 정보 입력(progress 갱신) 성공 - 처음 학습한 경우")
////        @Test
////        public void addChapterProgressSuccessFirst() {
////            ProgressDto dto = new ProgressDto(ch1.getNumber(), ContentConst.TIME_FLOW_QUESTION);
////
////            ResponseEntity<Void> response = restTemplate
////                    .withBasicAuth("customer1", "customer1")
////                    .postForEntity(URL, dto, Void.class);
////
////            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
////            Optional<ChapterSection> chapterProgressOptional = chapterSectionRepository.queryChapterProgress(customer1.getId(), ch1.getNumber());
////            assertThat(chapterProgressOptional.get().getContent()).isEqualTo(ContentConst.TIME_FLOW_QUESTION);
////        }
////        @DisplayName("단원 학습 정보 입력 실패 - DTO Validation")
////        @Test
////        public void addChapterProgressFailWrongDto() {
////            //필수조건인 단원번호, progresss 생략
////            ProgressDto dto = new ProgressDto();
////            ResponseEntity<List<ErrorDto>> response = restTemplate
////                    .withBasicAuth("customer1", "customer1")
////                    .exchange(URL, HttpMethod.POST, new HttpEntity<>(dto), new ParameterizedTypeReference<List<ErrorDto>>() {
////                    });
////
////            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
////            assertThat(response.getBody().size()).isEqualTo(2);
////        }
////
////        @DisplayName("단원 학습 정보 입력 실패 - 존재하지 않는 단원 번호 입력")
////        @Test
////        public void addChapterProgressFailNotFoundChapterNum(){
////            ProgressDto dto = new ProgressDto(-1, ContentConst.GET_TOPIC_BY_KEYWORD);
////            ResponseEntity<List<ErrorMsgDto>> response = restTemplate
////                    .withBasicAuth("customer1", "customer1")
////                    .exchange(URL, HttpMethod.POST, new HttpEntity<>(dto), new ParameterizedTypeReference<List<ErrorMsgDto>>() {
////                    });
////
////            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
////            assertThat(response.getBody().size()).isEqualTo(1);
////            assertThat(response.getBody().get(0)).usingRecursiveComparison().isEqualTo(new ErrorMsgDto(ErrorCode.CHAPTER_NOT_FOUND.getErrorMessage()));
////        }
////
////        @DisplayName("단원 학습 정보 입력 실패 - 존재하지 않는 progress 입력")
////        @Test
////        public void addChapterProgressFailNotFoundProgressConst(){
////            ProgressDto dto = new ProgressDto(ch1.getNumber(), "NOT IN PROGRESS CONST");
////            ResponseEntity<List<ErrorMsgDto>> response = restTemplate
////                    .withBasicAuth("customer1", "customer1")
////                    .exchange(URL, HttpMethod.POST, new HttpEntity<>(dto), new ParameterizedTypeReference<List<ErrorMsgDto>>() {
////                    });
////
////            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
////            assertThat(response.getBody().size()).isEqualTo(1);
////            assertThat(response.getBody().get(0)).usingRecursiveComparison().isEqualTo(new ErrorMsgDto(ErrorCode.PROGRESS_NOT_FOUND.getErrorMessage()));
////        }
////    }
////
////    @Nested
////    @DisplayName("주제 학습 정보 입력 - POST /study-progress/topic/wrong-count")
////    @TestInstance(PER_CLASS)
////    public class addTopicProgress{
////        @BeforeAll
////        public void init(){
////            suffix = "/study-progress/topic/wrong-count";
////            initConfig();
////        }
////
////        @AfterEach
////        public void clear(){
////            baseClear();
////        }
////
////        @BeforeEach
////        public void setting() {
////            baseSetting();
////        }
////
////        @DisplayName("주제 학습 정보 입력 성공 - 처음 학습한 경우")
////        @Test
////        public void addTopicProgressSuccessFirst() {
////            TopicProgressAddDto dto1 = new TopicProgressAddDto( t1.getTitle(), 5);
////            TopicProgressAddDto dto2 = new TopicProgressAddDto( t2.getTitle(), 50);
////
////            ResponseEntity<Void> response = restTemplate
////                    .withBasicAuth("customer1", "customer1")
////                    .postForEntity(URL, new TopicProgressAddDtoList(Arrays.asList(dto1,dto2)), Void.class);
////
////            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
////            TopicProgress topicProgress1 = topicProgressRepository.queryTopicProgress(t1.getTitle(), customer1.getId())
////                    .orElseThrow();
////            assertThat(topicProgress1.getWrongCount()).isEqualTo(8); // 기존3 + 추가5
////            TopicProgress topicProgress2 = topicProgressRepository.queryTopicProgress(t2.getTitle(), customer1.getId()).orElseThrow();
////            assertThat(topicProgress2.getWrongCount()).isEqualTo(50); // 기존0 + 추가 50
////        }
////
////        @DisplayName("주제 학습 정보 입력 성공 - 이전에 학습한적이 있는 경우")
////        @Test
////        public void addTopicProgressSuccessNotFirst() {
////            TopicProgressAddDto dto = new TopicProgressAddDto(t1.getTitle(), 5);
////            ResponseEntity<Void> response = restTemplate
////                    .withBasicAuth("customer1", "customer1")
////                    .postForEntity(URL, new TopicProgressAddDtoList(Arrays.asList(dto)), Void.class);
////
////            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
////            TopicProgress topicProgress = topicProgressRepository.queryTopicProgress(t1.getTitle(), customer1.getId()).orElseThrow();
////            assertThat(topicProgress.getWrongCount()).isEqualTo(8); // 기존 3 + 추가된 5
////        }
////
////        @DisplayName("주제 학습 정보 입력 실패 - DTO Validation")
////        @Test
////        public void addTopicProgressFailWrongDto() {
////            //필수조건인 회원아이디, 토픽제목 생략
////            TopicProgressAddDto dto = new TopicProgressAddDto();
////            ResponseEntity<List<ErrorDto>> response = restTemplate
////                    .withBasicAuth("customer1", "customer1")
////                    .exchange(URL, HttpMethod.POST, new HttpEntity<>(new TopicProgressAddDtoList(Arrays.asList(dto))), new ParameterizedTypeReference<List<ErrorDto>>() {
////            });
////
////            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
////            assertThat(response.getBody().size()).isEqualTo(1);
////        }
////
////        @DisplayName("주제 학습 정보 입력 실패 - 존재하지 않는 주제 제목 입력")
////        @Test
////        public void addChapterProgressFailNotFoundChapterNum(){
////            TopicProgressAddDto dto = new TopicProgressAddDto("title-2-2-2-2-2", 5);
////            ResponseEntity<List<ErrorMsgDto>> response = restTemplate
////                    .withBasicAuth("customer1", "customer1")
////                    .exchange(URL, HttpMethod.POST, new HttpEntity<>(new TopicProgressAddDtoList(Arrays.asList(dto))), new ParameterizedTypeReference<List<ErrorMsgDto>>() {
////            });
////
////            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
////            assertThat(response.getBody().size()).isEqualTo(1);
////            assertThat(response.getBody().get(0)).usingRecursiveComparison().isEqualTo(new ErrorMsgDto(ErrorCode.TOPIC_NOT_FOUND.getErrorMessage()));
////        }
////    }
//
//
//
//}
