package Project.OpenBook.Controller;//package Project.OpenBook.Controller;
//
//import Project.OpenBook.Constants.Role;
//import Project.OpenBook.Domain.*;
//import Project.OpenBook.Dto.primaryDate.PrimaryDateDto;
//import Project.OpenBook.Dto.primaryDate.PrimaryDateUserDto;
//import Project.OpenBook.Dto.sentence.SentenceDto;
//import Project.OpenBook.Dto.choice.ChoiceDto;
//import Project.OpenBook.Dto.description.DescriptionDto;
//import Project.OpenBook.Dto.error.ErrorDto;
//import Project.OpenBook.Dto.error.ErrorMsgDto;
//import Project.OpenBook.Dto.keyword.KeywordDto;
//import Project.OpenBook.Dto.keyword.KeywordUserDto;
//import Project.OpenBook.Dto.topic.TopicAdminDto;
//import Project.OpenBook.Dto.topic.TopicCustomerDto;
//import Project.OpenBook.Repository.category.CategoryRepository;
//import Project.OpenBook.Chapter.Repo.ChapterRepository;
//import Project.OpenBook.Repository.chaptersection.ChapterSectionRepository;
//import Project.OpenBook.Repository.choice.ChoiceRepository;
//import Project.OpenBook.Repository.customer.CustomerRepository;
//import Project.OpenBook.Repository.description.DescriptionRepository;
//import Project.OpenBook.Repository.dupdate.DupDateRepository;
//import Project.OpenBook.Repository.keyword.KeywordRepository;
//import Project.OpenBook.Repository.primarydate.PrimaryDateRepository;
//import Project.OpenBook.Repository.sentence.SentenceRepository;
//import Project.OpenBook.Repository.topic.TopicRepository;
//import Project.OpenBook.Repository.topicprogress.TopicProgressRepository;
//import org.junit.jupiter.api.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.core.ParameterizedTypeReference;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.test.context.TestPropertySource;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import static Project.OpenBook.Constants.ErrorCode.*;
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@TestPropertySource(properties = { "spring.config.location=classpath:application-test.yml" })
//class TopicControllerTest {
//    @LocalServerPort
//    int port;
//    @Autowired
//    private TopicRepository topicRepository;
//    @Autowired
//    private ChapterRepository chapterRepository;
//    @Autowired
//    private CategoryRepository categoryRepository;
//    @Autowired private DupDateRepository dupDateRepository;
//
//    @Autowired
//    KeywordRepository keywordRepository;
//
//    @Autowired
//    SentenceRepository sentenceRepository;
//    @Autowired
//    CustomerRepository customerRepository;
//
//    @Autowired
//    ChapterSectionRepository chapterSectionRepository;
//
//
//    @Autowired
//    PrimaryDateRepository primaryDateRepository;
//
//    @Autowired
//    TopicProgressRepository topicProgressRepository;
//
//    @Autowired
//    ChoiceRepository choiceRepository;
//
//    @Autowired
//    DescriptionRepository descriptionRepository;
//    @Autowired
//    PasswordEncoder passwordEncoder;
//    @Autowired
//    TestRestTemplate restTemplate;
//
//    private final String prefix = "http://localhost:";
//    private String suffix;
//    private String URL;
//
//    private String imageUrl = "../testImage";
//
//    @Value("${base.url}")
//    private String baseUrl;
//
//    private Customer customer1, customer2;
//    private Topic t1,t2;
//
//    private Chapter ch1;
//    private Category c1;
//
//    private Keyword k1,k2,k3;
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
//        c1 = new Category("c1");
//        categoryRepository.saveAndFlush(c1);
//
//        ch1 = new Chapter("ch1", 1);
//        chapterRepository.saveAndFlush(ch1);
//
//        chapterSectionRepository.saveAndFlush(new ChapterSection(customer1, ch1));
//
//        t1 = new Topic("title1", 100, 200, false,false,0, 0, "detail1", ch1, c1);
//        t2 = new Topic("title2", 300, 400, false,false,0, 0, "detail2", ch1, c1);
//        topicRepository.saveAndFlush(t1);
//        topicRepository.saveAndFlush(t2);
//
//        k1 = new Keyword("k1","c1",t1,imageUrl);
//        k2 = new Keyword("k2","c2", t1,null);
//        k3 = new Keyword("k3","c3",t1,null);
//        keywordRepository.saveAllAndFlush(Arrays.asList(k1, k2, k3));
//
//        PrimaryDate date1 = new PrimaryDate(133301111, true, "comment1", t1);
//        PrimaryDate date2 = new PrimaryDate(13330101, false, "comment1", t1);
//        primaryDateRepository.saveAllAndFlush(Arrays.asList(date1, date2));
//    }
//
//    private void baseClear() {
//        topicProgressRepository.deleteAllInBatch();
//        chapterSectionRepository.deleteAllInBatch();
//        customerRepository.deleteAllInBatch();
//        primaryDateRepository.deleteAllInBatch();
//        keywordRepository.deleteAllInBatch();
//        dupDateRepository.deleteAllInBatch();
//        topicRepository.deleteAllInBatch();
//        chapterRepository.deleteAllInBatch();
//        categoryRepository.deleteAllInBatch();
//    }
//
//    @Nested
//    @DisplayName("특정 토픽 상세정보 조회(관리자) - GET admin/topics/{topicTitle}")
//    @TestInstance(PER_CLASS)
//    public class queryTopicAdmin{
//        @BeforeAll
//        public void init(){
//            suffix = "/admin/topics/";
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
//        @DisplayName("특정 토픽 상세정보 조회 성공")
//        @Test
//        public void queryTopicSuccess() {
//            ResponseEntity<TopicAdminDto> response = restTemplate.getForEntity(URL + "title1", TopicAdminDto.class);
//
//            List<PrimaryDateDto> dateDtoList = primaryDateRepository.findAll().stream()
//                    .map(p -> new PrimaryDateDto(p.getExtraDate(), p.getExtraDateCheck(), p.getExtraDateComment()))
//                    .collect(Collectors.toList());
//
//            TopicAdminDto expectResult = new TopicAdminDto(1, "title1", "c1", 100, false,false,200, "detail1",dateDtoList);
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(expectResult);
//        }
//
//        @DisplayName("존재하지 않는 토픽 조회 요청")
//        @Test
//        public void queryTopicFail() {
//            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL + "title-1", HttpMethod.GET,
//                    null, new ParameterizedTypeReference<List<ErrorMsgDto>>() {});
//
//            //원하는 exception이 터졌는지 확인
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto(TOPIC_NOT_FOUND.getErrorMessage())));
//        }
//    }
//
//    @Nested
//    @DisplayName("특정 토픽 상세정보 조회(사용자) - GET /topics/{topicTitle}")
//    @TestInstance(PER_CLASS)
//    public class queryTopicCustomer{
//        @BeforeAll
//        public void init(){
//            suffix = "/topics/";
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
//        @DisplayName("특정 토픽 상세정보 조회 성공")
//        @Test
//        public void queryTopicCustomerSuccess() {
//            ResponseEntity<TopicCustomerDto> response = restTemplate
//                    .withBasicAuth("customer1", "customer1")
//                    .getForEntity(URL + "title1", TopicCustomerDto.class);
//
//            List<PrimaryDateUserDto> dateDtoList = primaryDateRepository.queryDatesByTopic(t1.getId()).stream()
//                    .map(p -> new PrimaryDateUserDto(p.getExtraDate(), p.getExtraDateComment()))
//                    .collect(Collectors.toList());
//
//            List<KeywordUserDto> keywordList = keywordRepository.queryKeywordsByTopic(t1.getTitle()).stream()
//                    .map(k -> new KeywordUserDto(k.getName(), k.getComment(), k.getImageUrl()))
//                    .collect(Collectors.toList());
//
//            List<String> sentenceList = sentenceRepository.queryByTopicTitle(t1.getTitle()).stream()
//                    .map(Sentence::getName)
//                    .collect(Collectors.toList());
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//            TopicCustomerDto body = response.getBody();
//            assertThat(body.getStartDate()).isEqualTo(t1.getStartDate());
//            assertThat(body.getEndDate()).isEqualTo(t1.getEndDate());
//            assertThat(body.getCategory()).isEqualTo(c1.getName());
//            assertThat(body.getExtraDateList().containsAll(dateDtoList)).isTrue();
//            assertThat(body.getKeywordList().containsAll(keywordList)).isTrue();
//            assertThat(body.getSentenceList().containsAll(sentenceList)).isTrue();
//        }
//
//        @DisplayName("존재하지 않는 토픽 조회 요청")
//        @Test
//        public void queryTopicCustomerFail() {
//            ResponseEntity<List<ErrorMsgDto>> response = restTemplate
//                    .withBasicAuth("customer1", "customer1")
//                    .exchange(URL + "title-1", HttpMethod.GET,
//                    null, new ParameterizedTypeReference<List<ErrorMsgDto>>() {});
//
//            //원하는 exception이 터졌는지 확인
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto(TOPIC_NOT_FOUND.getErrorMessage())));
//        }
//    }
//
//
//    @Nested
//    @DisplayName("특정 토픽의 전체 키워드 조회 - GET /topics/{topicTitle}/keywords")
//    @TestInstance(PER_CLASS)
//    public class queryTopicKeyword{
//        @BeforeAll
//        public void init(){
//            suffix = "/topics/";
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
//        @DisplayName("특정 토픽의 전체 키워드 조회 성공")
//        @Test
//        public void queryTopicSuccess() {
//            ResponseEntity<List<KeywordDto>> response = restTemplate.exchange(URL + "title1/keywords", HttpMethod.GET, null, new ParameterizedTypeReference<List<KeywordDto>>() {
//            });
//
//            KeywordDto dto1 = new KeywordDto(k1.getName(), k1.getComment(), imageUrl, k1.getId());
//            KeywordDto dto2 = new KeywordDto(k2.getName(), k2.getComment(), null, k2.getId());
//            KeywordDto dto3 = new KeywordDto(k3.getName(), k3.getComment(), null, k3.getId());
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(dto1, dto2, dto3));
//        }
//
//        @DisplayName("특정 토픽의 전체 키워드 조회 실패 - 존재하지 않는 토픽 제목 입력하기")
//        @Test
//        public void queryTopicFail(){
//            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL + "title-1/keywords", HttpMethod.GET,
//                    null, new ParameterizedTypeReference<List<ErrorMsgDto>>() {});
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto(TOPIC_NOT_FOUND.getErrorMessage())));
//        }
//    }
//
//    @Nested
//    @DisplayName("특정 토픽의 전체 문장 조회 - GET /topics/{topicTitle}/sentences")
//    @TestInstance(PER_CLASS)
//    public class queryTopicSentence{
//
//        private Sentence s1,s2;
//        @BeforeAll
//        public void init(){
//            suffix = "/topics/";
//            initConfig();
//        }
//
//        @AfterEach
//        public void clear(){
//            sentenceRepository.deleteAllInBatch();
//            baseClear();
//        }
//
//        @BeforeEach
//        public void setting() {
//            baseSetting();
//            s1 = new Sentence("sentence1",t1);
//            s2 = new Sentence("sentence2",t1);
//            sentenceRepository.saveAllAndFlush(Arrays.asList(s1, s2));
//
//        }
//        @DisplayName("특정 토픽의 전체 문장 조회 성공")
//        @Test
//        public void querySentencesSuccess() {
//            ResponseEntity<List<SentenceDto>> response = restTemplate.exchange(URL + "title1/sentences", HttpMethod.GET, null, new ParameterizedTypeReference<List<SentenceDto>>() {
//            });
//
//            String baseImageUrl = baseUrl + "/images/";
//
//            SentenceDto dto1 = new SentenceDto(s1.getName(), s1.getId());
//            SentenceDto dto2= new SentenceDto(s2.getName(), s2.getId());
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(dto1, dto2));
//        }
//
//        @DisplayName("특정 토픽의 전체 키워드 조회 실패 - 존재하지 않는 토픽 제목 입력하기")
//        @Test
//        public void queryTopicFail(){
//            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL + "title-1/keywords", HttpMethod.GET,
//                    null, new ParameterizedTypeReference<List<ErrorMsgDto>>() {});
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto(TOPIC_NOT_FOUND.getErrorMessage())));
//        }
//    }
//
//    @Nested
//    @DisplayName("특정 토픽의 모든 선지 조회 - GET /admin/topics/{topicTitle}/choices/")
//    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
//    public class queryTopicsChoices{
//
//        private Choice choice1, choice2, choice3, choice4, choice5;
//        @BeforeAll
//        public void init(){
//            suffix = "/admin/topics/";
//            initConfig();
//        }
//
//        @BeforeEach
//        public void setting(){
//            baseSetting();
//            /**
//             * choice1,2,3 -> topic1
//             * choice4,5 -> topic2
//             */
//            choice1 = new Choice("choice1", t1);
//            choice2 = new Choice("choice2", t1);
//            choice3 = new Choice("choice3", t1);
//
//            choice4 = new Choice("choice4", t2);
//            choice5 = new Choice("choice5", t2);
//
//            choiceRepository.saveAllAndFlush(Arrays.asList(choice1, choice2, choice3, choice4, choice5));
//        }
//
//        @AfterEach
//        public void clear(){
//            choiceRepository.deleteAllInBatch();
//            baseClear();
//        }
//
//        @DisplayName("특정 토픽의 모든 선지를 조회 성공")
//        @Test
//        public void queryChoicesInTopicsSuccess() {
//            String topicTitle = "topic1";
//            ResponseEntity<List<ChoiceDto>> response = restTemplate.exchange(URL + "title1/choices/", HttpMethod.GET, null, new ParameterizedTypeReference<List<ChoiceDto>>() {}, topicTitle);
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//            List<ChoiceDto> expectBody = Arrays.asList(new ChoiceDto(choice1), new ChoiceDto(choice2), new ChoiceDto(choice3));
//            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(expectBody);
//        }
//
//        //TODO : 선지 조회 실패 로직
//    }
//
//    @Nested
//    @DisplayName("특정 토픽의 모든 보기 조회 - GET /admin/topics/{topicTitle}/descriptions/")
//    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
//    public class queryTopicDescriptions{
//
//        private Description desc1, desc2, desc3, desc4, desc5;
//        @BeforeAll
//        public void init(){
//            suffix = "/topics/";
//            initConfig();
//        }
//
//        @BeforeEach
//        public void setting(){
//            baseSetting();
//            desc1 = new Description("desc1", t1);
//            desc2 = new Description("desc2", t1);
//            desc3 = new Description("desc3", t1);
//
//            desc4 = new Description("desc4", t2);
//            desc5 = new Description("desc5", t2);
//
//            descriptionRepository.saveAllAndFlush(Arrays.asList(desc1, desc2, desc3, desc4, desc5));
//        }
//
//        @AfterEach
//        public void clear(){
//            descriptionRepository.deleteAllInBatch();
//            baseClear();
//        }
//
//        @DisplayName("특정 토픽의 모든 보기를 조회 성공")
//        @Test
//        public void queryDescriptionsInTopicsSuccess() {
//            String topicTitle = "topic1";
//            ResponseEntity<List<DescriptionDto>> response = restTemplate.exchange(URL + "title1/descriptions/", HttpMethod.GET, null, new ParameterizedTypeReference<List<DescriptionDto>>() {}, topicTitle);
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//            List<DescriptionDto> expectBody = Arrays.asList(new DescriptionDto(desc1), new DescriptionDto(desc2), new DescriptionDto(desc3));
//            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(expectBody);
//        }
//    }
//    @Nested
//    @DisplayName("토픽 추가 - POST /admin/topics")
//    @TestInstance(PER_CLASS)
//    public class createTopic{
//
//        private int prevSize;
//        @BeforeAll
//        public void init(){
//            suffix = "/admin/topics";
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
//            prevSize = topicRepository.findAll().size();
//        }
//        @DisplayName("토픽 생성 성공")
//        @Test
//        public void queryKeywordsSuccess() {
//            PrimaryDateDto dateDto = new PrimaryDateDto(13330111, true, "newComment1");
//            TopicAdminDto topicAdminDto = new TopicAdminDto(1, "title33", "c1", 19980318,false,false, 20230321, "detail2",Arrays.asList(dateDto));
//
//            ResponseEntity<Void> response = restTemplate.postForEntity(URL, topicAdminDto, Void.class);
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
//            //토픽이 잘 생성되었는지 확인
//            Topic topic = topicRepository.findTopicByTitle("title33").get();
//            assertThat(topicRepository.findAll().size()).isEqualTo(prevSize + 1);
//
//            //연표에 나올 연도가 잘 입력되었는지 확인
//            List<PrimaryDate> dateList = primaryDateRepository.queryDatesByTopic(topic.getId());
//            assertThat(dateList.size()).isEqualTo(1);
//            PrimaryDate primaryDate = dateList.get(0);
//            assertThat(new PrimaryDateDto(primaryDate.getExtraDate(), primaryDate.getExtraDateCheck(), primaryDate.getExtraDateComment()))
//                    .usingRecursiveComparison().isEqualTo(dateDto);
//        }
//
//        @DisplayName("토픽 생성 실패 - DTO validation")
//        @Test
//        public void createTopicFailWrongDTO(){
//            //필수 입력 조건인 chapterNum,title,categoryName 생략
//            TopicAdminDto topicAdminDto = new TopicAdminDto();
//            ResponseEntity<List<ErrorDto>> response = restTemplate.exchange(URL, HttpMethod.POST, new HttpEntity<>(topicAdminDto), new ParameterizedTypeReference<List<ErrorDto>>() {
//            });
//
//            //필수 입력 조건에 대한 오류메세지가 3개 들어갔는지 확인
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
//            assertThat(response.getBody().size()).isEqualTo(3);
//
//            //토픽이 생성되지 않음을 확인
//            assertThat(topicRepository.findAll().size()).isEqualTo(prevSize);
//        }
//
//        @DisplayName("새로운 토픽 추가 실패 - 제목이 중복되는 경우")
//        @Test
//        public void createTopicFailDupTitle() {
//            TopicAdminDto wrongDto = new TopicAdminDto(1, "title1", "c1", 0,false,false, 0, "detail123",null);
//            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL, HttpMethod.POST,
//                    new HttpEntity<>(wrongDto), new ParameterizedTypeReference<List<ErrorMsgDto>>() {});
//
//            //원하는 exception이 터졌는지 확인
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
//            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto(DUP_TOPIC_TITLE.getErrorMessage())));
//
//            //토픽이 생성되지 않음을 확인
//            assertThat(topicRepository.findAll().size()).isEqualTo(prevSize);
//        }
//
//
//        @DisplayName("새로운 토픽 추가 실패 - 존재하지 않는 단원 번호를 입력하는 경우")
//        @Test
//        public void createTopicFailWNotExistChapter() {
//            TopicAdminDto wrongDto = new TopicAdminDto(123, "title123", "c1", 0,false,false, 0, "detail123",null);
//            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL, HttpMethod.POST,
//                    new HttpEntity<>(wrongDto), new ParameterizedTypeReference<List<ErrorMsgDto>>() {});
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto(CHAPTER_NOT_FOUND.getErrorMessage())));
//
//            //토픽이 생성되지 않음을 확인
//            assertThat(topicRepository.findAll().size()).isEqualTo(prevSize);
//        }
//
//        @DisplayName("새로운 토픽 추가 실패 - 존재하지 않는 카테고리 이름을 입력하는 경우")
//        @Test
//        public void createTopicFailNotExistCategory() {
//            TopicAdminDto wrongDto = new TopicAdminDto(1, "title123", "c-1", 0,false,false, 0, "detail123",null);
//            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL, HttpMethod.POST,
//                    new HttpEntity<>(wrongDto), new ParameterizedTypeReference<List<ErrorMsgDto>>() {});
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto(CATEGORY_NOT_FOUND.getErrorMessage())));
//
//            //토픽이 생성되지 않음을 확인
//            assertThat(topicRepository.findAll().size()).isEqualTo(prevSize);
//        }
//
//    }
//
//    //TODO : 문장
//
//    @Nested
//    @DisplayName("토픽 상세정보 수정 - PATCH /admin/topics/{topicTitle}")
//    @TestInstance(PER_CLASS)
//    public class updateTopic{
//
//        private int prevSize;
//        private List<PrimaryDateDto> dateDtoList = new ArrayList<>();
//
//        @BeforeAll
//        public void init(){
//            suffix = "/admin/topics/";
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
//            prevSize = topicRepository.findAll().size();
//            Category c2 = new Category("c2");
//            categoryRepository.saveAndFlush(c2);
//            dateDtoList = Arrays.asList(new PrimaryDateDto(19980318, true, "birthday!"));
//        }
//
//        @DisplayName("기존 상세정보 변경 성공")
//        @Test
//        public void updateTopicSuccess() {
//            TopicAdminDto dto = new TopicAdminDto(1, "title3", "c2", -1000,false,false, 1000,"detail123",dateDtoList);
//
//            ResponseEntity<Void> response = restTemplate.exchange(URL + "title1", HttpMethod.PATCH, new HttpEntity<>(dto), Void.class);
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//            //입력한 내용대로 내용이 변화했는지 확인
//            assertThat(topicRepository.findTopicByTitle("title1").isEmpty()).isTrue();
//            assertThat(topicRepository.findTopicByTitle("title3").isPresent()).isTrue();
//            assertThat(topicRepository.queryTopicAdminDto("title3")).usingRecursiveComparison().isEqualTo(dto);
//
//            //토픽의 전체 개수에는 변화가 없음을 확인
//            assertThat(topicRepository.findAll().size()).isEqualTo(prevSize);
//        }
//
//        @DisplayName("기존 상세정보 변경 실패 - DTO Validation")
//        @Test
//        public void updateTopicFailWrongDto() {
//            //필수 입력 조건인 chapterNum,title,categoryName 생략
//            TopicAdminDto topicAdminDto = new TopicAdminDto();
//            ResponseEntity<List<ErrorDto>> response = restTemplate.exchange(URL + "title1", HttpMethod.PATCH, new HttpEntity<>(topicAdminDto), new ParameterizedTypeReference<List<ErrorDto>>() {
//            });
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
//            assertThat(response.getBody().size()).isEqualTo(3);
//
//            //토픽의 전체 개수에는 변화가 없음을 확인
//            assertThat(topicRepository.findAll().size()).isEqualTo(prevSize);
//        }
//
//        @DisplayName("기존 토픽 변경 실패 - 중복된 제목을 입력하는 경우")
//        @Test
//        public void updateTopicFailDupTitle() {
//            TopicAdminDto wrongDto = new TopicAdminDto(1, "title2", "c1", 0,false,false, 0,"detail123",dateDtoList);
//            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL + "title1", HttpMethod.PATCH,
//                    new HttpEntity<>(wrongDto), new ParameterizedTypeReference<List<ErrorMsgDto>>() {});
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
//            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto(DUP_TOPIC_TITLE.getErrorMessage())));
//
//            //토픽의 전체 개수에는 변화가 없음을 확인
//            assertThat(topicRepository.findAll().size()).isEqualTo(prevSize);
//        }
//
//
//
//        @DisplayName("기존 토픽 변경 실패 - 존재하지 않는 단원 입력")
//        @Test
//        public void updateTopicFailNotExistChapter() {
//            TopicAdminDto wrongDto = new TopicAdminDto(123, "title123", "c1", 0,false,false, 0, "detail123",dateDtoList);
//            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL + "title1", HttpMethod.PATCH,
//                    new HttpEntity<>(wrongDto), new ParameterizedTypeReference<List<ErrorMsgDto>>() {});
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto("존재하지 않는 단원 번호입니다.")));
//
//            //토픽의 전체 개수에는 변화가 없음을 확인
//            assertThat(topicRepository.findAll().size()).isEqualTo(prevSize);
//        }
//
//        @DisplayName("기존 토픽 변경 실패 - 존재하지 않는 카테고리 입력 ")
//        @Test
//        public void updateTopicFailNotExistCategory() {
//            TopicAdminDto wrongDto = new TopicAdminDto(1, "title123", "c-1", 0,false,false, 0, "detail123",dateDtoList);
//            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL + "title1", HttpMethod.PATCH,
//                    new HttpEntity<>(wrongDto), new ParameterizedTypeReference<List<ErrorMsgDto>>() {});
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto("존재하지 않는 카테고리 제목입니다.")));
//
//            //토픽의 전체 개수에는 변화가 없음을 확인
//            assertThat(topicRepository.findAll().size()).isEqualTo(prevSize);
//        }
//    }
//
//    @Nested
//    @DisplayName("토픽 삭제 - DELETE /admin/topics/{topicTitle}")
//    @TestInstance(PER_CLASS)
//    public class deleteTopic{
//
//        private int prevSize;
//        private Topic t3,t4;
//        @BeforeAll
//        public void init(){
//            suffix = "/admin/topics/";
//            initConfig();
//        }
//
//        @AfterEach
//        public void clear(){
//            choiceRepository.deleteAllInBatch();
//            descriptionRepository.deleteAllInBatch();
//            baseClear();
//        }
//
//        @BeforeEach
//        public void setting() {
//            baseSetting();
//
//            prevSize = topicRepository.findAll().size();
//        }
//
//        @DisplayName("토픽 삭제 성공")
//        @Test
//        public void deleteTopicSuccess() {
//            ResponseEntity<Void> response = restTemplate.exchange(URL + "title2", HttpMethod.DELETE, null, Void.class);
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//            assertThat(topicRepository.findTopicByTitle("title2").isEmpty()).isTrue();
//            assertThat(topicRepository.findAll().size()).isEqualTo(prevSize - 1);
//        }
//
//        @DisplayName("토픽 삭제 실패 - 존재하지 않는 토픽 제목 입력")
//        @Test
//        public void deleteNotExistTopicFail() {
//            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL + "/title3", HttpMethod.DELETE,
//                    null, new ParameterizedTypeReference<List<ErrorMsgDto>>() {});
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto(TOPIC_NOT_FOUND.getErrorMessage())));
//            assertThat(topicRepository.findTopicByTitle("title1").isPresent()).isTrue();
//
//            assertThat(topicRepository.findAll().size()).isEqualTo(prevSize);
//        }
//
//        @DisplayName("토픽 삭제 실패 - 키워드가 존재하는 토픽 삭제 시도")
//        @Test
//        public void deleteTopicHasKeywordFail() {
//            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL + "/title1", HttpMethod.DELETE,
//                    null, new ParameterizedTypeReference<List<ErrorMsgDto>>() {});
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
//            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto(TOPIC_HAS_KEYWORD.getErrorMessage())));
//            assertThat(topicRepository.findTopicByTitle("title1").isPresent()).isTrue();
//        }
//
//        @DisplayName("토픽 삭제 실패 - 선지가 존재하는 경우")
//        @Test
//        public void deleteTopicHasChoiceFail() {
//            Choice choice1 = new Choice("ch1", t2);
//            choiceRepository.save(choice1);
//            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL + "/title2", HttpMethod.DELETE,
//                    null, new ParameterizedTypeReference<List<ErrorMsgDto>>() {});
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
//            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto(TOPIC_HAS_CHOICE.getErrorMessage())));
//            assertThat(topicRepository.findTopicByTitle("title1").isPresent()).isTrue();
//        }
//
//        @DisplayName("토픽 삭제 실패 - 보기가 존재하는 경우")
//        @Test
//        public void deleteTopicHasDescFail() {
//            Description desc1 = new Description("desc1", t2);
//            descriptionRepository.save(desc1);
//            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL + "/title2", HttpMethod.DELETE,
//                    null, new ParameterizedTypeReference<List<ErrorMsgDto>>() {});
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
//            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto(TOPIC_HAS_DESCRIPTION.getErrorMessage())));
//            assertThat(topicRepository.findTopicByTitle("title1").isPresent()).isTrue();
//        }
//
//    }
//}