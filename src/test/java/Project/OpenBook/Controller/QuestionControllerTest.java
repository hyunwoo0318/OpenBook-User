package Project.OpenBook.Controller;//package Project.OpenBook.Controller;
//
//import Project.OpenBook.Constants.ErrorCode;
//import Project.OpenBook.Constants.Role;
//import Project.OpenBook.Domain.*;
//import Project.OpenBook.Dto.error.ErrorMsgDto;
//import Project.OpenBook.Dto.keyword.KeywordNameCommentDto;
//import Project.OpenBook.Dto.question.*;
//import Project.OpenBook.Repository.category.CategoryRepository;
//import Project.OpenBook.Chapter.Repo.ChapterRepository;
//import Project.OpenBook.Repository.chaptersection.ChapterSectionRepository;
//import Project.OpenBook.Repository.customer.CustomerRepository;
//import Project.OpenBook.Repository.keyword.KeywordRepository;
//import Project.OpenBook.Repository.primarydate.PrimaryDateRepository;
//import Project.OpenBook.Repository.sentence.SentenceRepository;
//import Project.OpenBook.Repository.topic.TopicRepository;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.core.ParameterizedTypeReference;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.test.context.TestPropertySource;
//
//import java.util.*;
//
//import static Project.OpenBook.Constants.ErrorCode.CHAPTER_NOT_FOUND;
//import static Project.OpenBook.Constants.ErrorCode.TOPIC_NOT_FOUND;
//import static Project.OpenBook.Constants.QuestionConst.GET_TOPIC_WRONG_ANSWER_NUM;
//import static Project.OpenBook.Constants.QuestionConst.WRONG_KEYWORD_SENTENCE_NUM;
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
//
//
//
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@TestPropertySource(properties = { "spring.config.location=classpath:application-test.yml" })
//class QuestionControllerTest{
//
//    @LocalServerPort
//    int port;
//    @Autowired
//    private TopicRepository topicRepository;
//    @Autowired
//    private ChapterRepository chapterRepository;
//    @Autowired
//    private CategoryRepository categoryRepository;
//    @Autowired
//    PrimaryDateRepository primaryDateRepository;
//    @Autowired
//    private CustomerRepository customerRepository;
//    @Autowired
//    KeywordRepository keywordRepository;
//    @Autowired
//    SentenceRepository sentenceRepository;
//
//    @Autowired
//    ChapterSectionRepository chapterSectionRepository;
//
//    @Autowired
//    PasswordEncoder passwordEncoder;
//
//    @Autowired
//    TestRestTemplate restTemplate;
//
//    private final String prefix = "http://localhost:";
//    private String suffix;
//    private String URL;
//
//    private Customer customer1, customer2;
//
//    private Topic t1,t2,t3;
//
//    private Chapter ch1;
//    private Category c1;
//
//    private PrimaryDate date1, date2;
//    private Keyword k1,k2,k3,k4,k5,k6,k7,k8;
//    private Sentence s1,s2,s3,s4,s5,s6,s7,s8;
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
//        ChapterSection chapterSection = new ChapterSection(customer1, ch1);
//        chapterSectionRepository.saveAndFlush(chapterSection);
//
//        List<Topic> topicList = new ArrayList<>();
//        t1 = new Topic("title1", 100, 200, true,false,0, 0, "detail1", ch1, c1);
//        t2 = new Topic("title2", 300, 400, false, true, 0, 0, "detail2", ch1, c1);
//        t3 = new Topic("title3", 500, 600, false, false, 0, 0, "detail3", ch1, c1);
//        topicList.addAll(Arrays.asList(t1, t2, t3));
//
//        //추가적으로 토픽 20개 더 추가
//        for (int i = 4; i < 24; i++) {
//            Topic topic = new Topic("title" + i,0,0,false,false,0,0,"detail" + i, ch1, c1);
//            topicList.add(topic);
//        }
//        topicRepository.saveAllAndFlush(topicList);
//
//        date1 = new PrimaryDate(13330111, true, "comment1", t1);
//        date2 = new PrimaryDate(13330101, false, "comment2", t1);
//        primaryDateRepository.saveAllAndFlush(Arrays.asList(date1, date2));
//
//        /**
//         * t1 -> k1,k2, s1,s2
//         * t2 -> k3,k4,k5,k6,k7 s3,s4,s5,s6,s7
//         * t3 -> k8, s8
//         */
//
//        k1 = new Keyword("k1", "c1", t1,null);
//        k2 = new Keyword("k2", "c2", t1,null);
//        k3 = new Keyword("k3", "c3", t2,null);
//        k4 = new Keyword("k4", "c4", t2,null);
//        k5 = new Keyword("k5", "c5", t2,null);
//        k6 = new Keyword("k6", "c6", t2,null);
//        k7 = new Keyword("k7", "c7", t2,null);
//        k8 = new Keyword("k8", "c8", t3,null);
//        keywordRepository.saveAllAndFlush(Arrays.asList(k1, k2, k3, k4, k5, k6, k7, k8));
//
//        s1 = new Sentence("s1", t1);
//        s2 = new Sentence("s2", t1);
//        s3 = new Sentence("s3", t2);
//        s4 = new Sentence("s4", t2);
//        s5 = new Sentence("s5", t2);
//        s6 = new Sentence("s6", t2);
//        s7 = new Sentence("s7", t2);
//        s8 = new Sentence("s8", t3);
//        sentenceRepository.saveAllAndFlush(Arrays.asList(s1, s2, s3, s4, s5, s6,s7,s8));
//
//    }
//
//    private void baseClear() {
//        chapterSectionRepository.deleteAllInBatch();
//        customerRepository.deleteAllInBatch();
//        sentenceRepository.deleteAllInBatch();
//        keywordRepository.deleteAllInBatch();
//        primaryDateRepository.deleteAllInBatch();
//        topicRepository.deleteAllInBatch();
//        chapterRepository.deleteAllInBatch();
//        categoryRepository.deleteAllInBatch();
//    }
//
//    @Nested
//    @DisplayName("연표 제공 - GET /questions/time-flow")
//    @TestInstance(PER_CLASS)
//    public class queryTimeFlowQuestion{
//        @BeforeAll
//        public void init(){
//            suffix = "/questions/time-flow";
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
//        /**
//         * expectResult - [t1.startDate, t2.endDate, date1]
//         */
//        @DisplayName("연표 문제 제공 성공")
//        @Test
//        public void queryTimeFlowQuestion(){
//            TimeFlowQuestionDto dto1 = new TimeFlowQuestionDto(t1.getStartDate(), t1.getTitle() + "의 시작연도입니다.", t1.getTitle());
//            TimeFlowQuestionDto dto2 = new TimeFlowQuestionDto(t2.getEndDate(), t2.getTitle() + "의 종료연도입니다.", t2.getTitle());
//            TimeFlowQuestionDto dto3 = new TimeFlowQuestionDto(date1.getExtraDate(), date1.getExtraDateComment(), t1.getTitle());
//
//            ResponseEntity<List<TimeFlowQuestionDto>> response = restTemplate
//                    .withBasicAuth("customer1", "customer1")
//                    .exchange(URL + "?num=1&type=Question", HttpMethod.GET, null,
//                    new ParameterizedTypeReference<List<TimeFlowQuestionDto>>() {});
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//
//            List<TimeFlowQuestionDto> body = response.getBody();
//            assertThat(body.size()).isEqualTo(3);
//            assertThat(body).usingRecursiveComparison().isEqualTo(Arrays.asList(dto1, dto2, dto3));
//        }
//
//        @DisplayName("연표 학습 제공 성공")
//        @Test
//        public void queryTimeFlowStudy(){
//            TimeFlowQuestionDto dto1 = new TimeFlowQuestionDto(t1.getStartDate(), t1.getTitle() + "의 시작연도입니다.", t1.getTitle());
//            TimeFlowQuestionDto dto2 = new TimeFlowQuestionDto(t2.getEndDate(), t2.getTitle() + "의 종료연도입니다.", t2.getTitle());
//            TimeFlowQuestionDto dto3 = new TimeFlowQuestionDto(date1.getExtraDate(), date1.getExtraDateComment(), t1.getTitle());
//
//            ResponseEntity<List<TimeFlowQuestionDto>> response = restTemplate
//                    .withBasicAuth("customer1", "customer1")
//                    .exchange(URL + "?num=1&type=Study", HttpMethod.GET, null,
//                    new ParameterizedTypeReference<List<TimeFlowQuestionDto>>() {});
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//
//            List<TimeFlowQuestionDto> body = response.getBody();
//            assertThat(body.size()).isEqualTo(3);
//            assertThat(body).usingRecursiveComparison().isEqualTo(Arrays.asList(dto1, dto2, dto3));
//        }
//
//        @DisplayName("연표 제공 실패 - 존재하지 않는 단원번호 입력")
//        @Test
//        public void queryTimeFlowFail() {
//            ResponseEntity<List<ErrorMsgDto>> response = restTemplate
//                    .withBasicAuth("customer1", "customer1")
//                    .exchange(URL + "?num=-1&type=Question", HttpMethod.GET,
//                    null, new ParameterizedTypeReference<List<ErrorMsgDto>>() {});
//
//            Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//            Assertions.assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto(ErrorCode.CHAPTER_NOT_FOUND.getErrorMessage())));
//
//        }
//    }
//
//    @Nested
//    @DisplayName("주제보고 키워드 맞추기 문제 제공 - GET /questions/get-keywords")
//    @TestInstance(PER_CLASS)
//    public class queryGetKeywordsQuestion{
//        @BeforeAll
//        public void init(){
//            suffix = "/questions/get-keywords";
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
//        @DisplayName("주제보고 키워드/문장 맞추기 문제 제공 성공")
//        @Test
//        public void queryGetKeywordsQuestionSuccess(){
//            String topicTitle = t1.getTitle();
//            ResponseEntity<List<QuestionDto>> response = restTemplate
//                    .withBasicAuth("customer1", "customer1")
//                    .exchange(URL + "?title=" + topicTitle, HttpMethod.GET, null, new ParameterizedTypeReference<List<QuestionDto>>() {
//                    });
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//
//            List<QuestionDto> body = response.getBody();
//
//            //해당 주제의 키워드 개수만큼 문제가 생성되었는지 확인
//            assertThat(body.size()).isEqualTo(keywordRepository.queryKeywordsByTopic(topicTitle).size());
//
//            for (QuestionDto questionDto : body) {
//                //정답이 요청한 주제와 일치하는지 확인
//                assertThat(questionDto.getAnswer()).isEqualTo(topicTitle);
//
//                //정답 + 오답 전체 선택지 개수가 WRONG_ANSWER_NUM + 1인지 확인
//                List<QuestionChoiceDto> choiceList = questionDto.getChoiceList();
//                assertThat(choiceList.size()).isEqualTo(WRONG_KEYWORD_SENTENCE_NUM + 1);
//
//                //정답 주제의 키워드가 1개, 정답 주제가 아닌 다른 주제의 키워드가 WRONG_KEYWORD_SENTENCE_NUM개 있는지 확인
//                int answerCnt = 0;
//                int wrongAnswerCnt = 0;
//                for (QuestionChoiceDto questionChoiceDto : choiceList) {
//                    if(questionChoiceDto.getKey().equals(topicTitle)){
//                        answerCnt++;
//                    }else{
//                        wrongAnswerCnt++;
//                    }
//                }
//
//                assertThat(answerCnt).isEqualTo(1);
//                assertThat(wrongAnswerCnt).isEqualTo(WRONG_KEYWORD_SENTENCE_NUM);
//            }
//
//        }
//
//        @DisplayName("주제 보고 키워드 맞추기 문제 제공 성공 - 키워드가 부족한 경우")
//        @Test
//        public void queryGetKeywordsQuestionSuccessLessKeywordsSentence(){
//            String topicTitle = t2.getTitle();
//            ResponseEntity<List<QuestionDto>> response = restTemplate
//                    .withBasicAuth("customer1", "customer1")
//                    .exchange(URL + "?title=" + topicTitle, HttpMethod.GET, null, new ParameterizedTypeReference<List<QuestionDto>>() {
//                    });
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//
//            List<QuestionDto> body = response.getBody();
//
//            //해당 주제의 키워드 개수만큼 문제가 생성되었는지 확인
//            assertThat(body.size()).isEqualTo(keywordRepository.queryKeywordsByTopic(topicTitle).size());
//
//            for (QuestionDto questionDto : body) {
//                //정답이 요청한 주제와 일치하는지 확인
//                assertThat(questionDto.getAnswer()).isEqualTo(topicTitle);
//
//                //정답 + 오답 전체 선택지 개수가 WRONG_ANSWER_NUM + 1인지 확인
//                List<QuestionChoiceDto> choiceList = questionDto.getChoiceList();
//                assertThat(choiceList.size()).isEqualTo(WRONG_KEYWORD_SENTENCE_NUM + 1);
//
//                //정답 주제의 키워드가 1개, 정답 주제가 아닌 다른 주제의 키워드가 WRONG_KEYWORD_SENTENCE_NUM개 있는지 확인
//                int answerCnt = 0;
//                int wrongAnswerCnt = 0;
//                for (QuestionChoiceDto questionChoiceDto : choiceList) {
//                    if(questionChoiceDto.getKey().equals(topicTitle)){
//                        answerCnt++;
//                    }else{
//                        wrongAnswerCnt++;
//                    }
//                }
//
//                assertThat(answerCnt).isEqualTo(1);
//                assertThat(wrongAnswerCnt).isEqualTo(WRONG_KEYWORD_SENTENCE_NUM);
//            }
//        }
//
//        @DisplayName("주제보고 키워드 맞추기 문제 제공 실패 - 존재하지 않는 토픽 제목 입력")
//        @Test
//        public void queryGetKeywordsQuestionFailWrongTitle(){
//            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL + "?title=title99999", HttpMethod.GET,
//                    null, new ParameterizedTypeReference<List<ErrorMsgDto>>() {});
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto(TOPIC_NOT_FOUND.getErrorMessage())));
//        }
//
//    }
//
//    @Nested
//    @DisplayName("주제보고 문장 맞추기 문제 제공 - GET /questions/get-sentences")
//    @TestInstance(PER_CLASS)
//    public class queryGetSentencesQuestion{
//        @BeforeAll
//        public void init(){
//            suffix = "/questions/get-sentences";
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
//        @DisplayName("주제보고 문장 맞추기 문제 제공 성공")
//        @Test
//        public void queryGetSentencesQuestionSuccess(){
//            String topicTitle = t1.getTitle();
//            ResponseEntity<List<QuestionDto>> response = restTemplate
//                    .withBasicAuth("customer1", "customer1")
//                    .exchange(URL + "?title=" + topicTitle, HttpMethod.GET, null, new ParameterizedTypeReference<List<QuestionDto>>() {
//                    });
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//
//            List<QuestionDto> body = response.getBody();
//
//            //해당 주제의 문장 개수만큼 문제가 생성되었는지 확인
//            assertThat(body.size()).isEqualTo(sentenceRepository.queryByTopicTitle(topicTitle).size());
//
//            for (QuestionDto questionDto : body) {
//                //정답이 요청한 주제와 일치하는지 확인
//                assertThat(questionDto.getAnswer()).isEqualTo(topicTitle);
//
//                //정답 + 오답 전체 선택지 개수가 WRONG_ANSWER_NUM + 1인지 확인
//                List<QuestionChoiceDto> choiceList = questionDto.getChoiceList();
//                assertThat(choiceList.size()).isEqualTo(WRONG_KEYWORD_SENTENCE_NUM + 1);
//
//                //정답 주제의 문장이 1개, 정답 주제가 아닌 다른 주제의 문장이 WRONG_KEYWORD_SENTENCE_NUM개 있는지 확인
//                int answerCnt = 0;
//                int wrongAnswerCnt = 0;
//                for (QuestionChoiceDto questionChoiceDto : choiceList) {
//                    if(questionChoiceDto.getKey().equals(topicTitle)){
//                        answerCnt++;
//                    }else{
//                        wrongAnswerCnt++;
//                    }
//                }
//
//                assertThat(answerCnt).isEqualTo(1);
//                assertThat(wrongAnswerCnt).isEqualTo(WRONG_KEYWORD_SENTENCE_NUM);
//            }
//
//        }
//
//        @DisplayName("주제 보고 문장 맞추기 문제 제공 성공 - 문장수가 부족한 경우")
//        @Test
//        public void queryGetSentencesQuestionSuccessLessKeywordsSentence(){
//            String topicTitle = t2.getTitle();
//            ResponseEntity<List<QuestionDto>> response = restTemplate
//                    .withBasicAuth("customer1", "customer1")
//                    .exchange(URL + "?title=" + topicTitle, HttpMethod.GET, null, new ParameterizedTypeReference<List<QuestionDto>>() {
//                    });
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//
//            List<QuestionDto> body = response.getBody();
//
//            //해당 주제의 문장 개수만큼 문제가 생성되었는지 확인
//            assertThat(body.size()).isEqualTo(sentenceRepository.queryByTopicTitle(topicTitle).size());
//
//            for (QuestionDto questionDto : body) {
//                //정답이 요청한 주제와 일치하는지 확인
//                assertThat(questionDto.getAnswer()).isEqualTo(topicTitle);
//
//                //정답 + 오답 전체 선택지 개수가 WRONG_ANSWER_NUM + 1인지 확인
//                List<QuestionChoiceDto> choiceList = questionDto.getChoiceList();
//                assertThat(choiceList.size()).isEqualTo(WRONG_KEYWORD_SENTENCE_NUM + 1);
//
//                //정답 주제의 문장이 1개, 정답 주제가 아닌 다른 주제의 문장이 WRONG_KEYWORD_SENTENCE_NUM개 있는지 확인
//                int answerCnt = 0;
//                int wrongAnswerCnt = 0;
//                for (QuestionChoiceDto questionChoiceDto : choiceList) {
//                    if(questionChoiceDto.getKey().equals(topicTitle)){
//                        answerCnt++;
//                    }else{
//                        wrongAnswerCnt++;
//                    }
//                }
//
//                assertThat(answerCnt).isEqualTo(1);
//                assertThat(wrongAnswerCnt).isEqualTo(WRONG_KEYWORD_SENTENCE_NUM);
//            }
//        }
//
//        @DisplayName("주제보고 문장 맞추기 문제 제공 실패 - 존재하지 않는 토픽 제목 입력")
//        @Test
//        public void queryGeSentencesQuestionFailWrongTitle(){
//            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL + "?title=title99999", HttpMethod.GET,
//                    null, new ParameterizedTypeReference<List<ErrorMsgDto>>() {});
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto(TOPIC_NOT_FOUND.getErrorMessage())));
//        }
//    }
//
//    @Nested
//    @DisplayName("키워드 보고 주제 맞추기 문제 제공 - GET /questions/get-topics-keywords")
//    @TestInstance(PER_CLASS)
//    public class queryGetTopicsByKeywordQuestion{
//        @BeforeAll
//        public void init(){
//            suffix = "/questions/get-topics-keywords";
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
//        /**
//         * t1 -> keyword
//         * t2 -> keyword
//         * t3 -> keyword
//         * 총 2개의 문제가 생성되어야함
//         */
//        @DisplayName("키워드 보고 주제 맞추기 문제 제공 성공")
//        @Test
//        public void queryGetTopicsByKeywordQuestionSuccess(){
//            int num = ch1.getNumber();
//            ResponseEntity<List<QuestionDto>> response = restTemplate
//                    .withBasicAuth("customer1", "customer1")
//                    .exchange(URL + "?num=" + num, HttpMethod.GET, null, new ParameterizedTypeReference<List<QuestionDto>>() {
//                    });
//
//            List<QuestionDto> questionDtoList = response.getBody();
//
//            //총 2개의 문제가 생성되었는지 확인
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//            assertThat(questionDtoList.size()).isEqualTo(2); // t1,t2각각 1개씩의 문제 생성 (t3의 키워드 개수가 1개여서 생성 x)
//
//            for (QuestionDto questionDto : questionDtoList) {
//                String topicTitle = questionDto.getAnswer();
//
//                //보기로 제시되는 2개의 키워드가 answer 토픽 내에 존재하는지 확인
//                List<KeywordNameCommentDto> descriptionKeyword = questionDto.getDescriptionKeyword();
//                for (KeywordNameCommentDto dto : descriptionKeyword) {
//                    assertThat(keywordRepository.queryByNameInTopic(dto.getName(), topicTitle).isPresent()).isTrue();
//                }
//
//                //총 선지가 GET_TOPIC_WRONG_ANSWER_NUM + 1개인지 확인
//                List<QuestionChoiceDto> choiceList = questionDto.getChoiceList();
//                assertThat(choiceList.size()).isEqualTo(GET_TOPIC_WRONG_ANSWER_NUM + 1);
//
//                //정답 주제 1개, 오답 주제 GET_TOPIC_WRONG_ANSWER_NUM개인지 확인
//                int answerCnt = 0;
//                int wrongAnswerCnt = 0;
//                for (QuestionChoiceDto questionChoiceDto : choiceList) {
//                    if(questionChoiceDto.getKey().equals(topicTitle)){
//                        answerCnt++;
//                    }else{
//                        wrongAnswerCnt++;
//                    }
//                }
//
//                assertThat(answerCnt).isEqualTo(1);
//                assertThat(wrongAnswerCnt).isEqualTo(GET_TOPIC_WRONG_ANSWER_NUM);
//            }
//        }
//
//        @DisplayName("키워드 보고 주제 맞추기 문제 제공 실패 - 존재하지 않는 단원 번호 입력")
//        @Test
//        public void queryGetTopicsByKeywordQuestionFailWrongNum(){
//            ResponseEntity<List<ErrorMsgDto>> response = restTemplate
//                    .withBasicAuth("customer1", "customer1")
//
//                    .exchange(URL + "?num=123123123", HttpMethod.GET,
//                    null, new ParameterizedTypeReference<List<ErrorMsgDto>>() {});
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto(CHAPTER_NOT_FOUND.getErrorMessage())));
//
//        }
//    }
//
//    @Nested
//    @DisplayName("문장 보고 주제 맞추기 문제 제공 - GET /questions/get-topics-sentences")
//    @TestInstance(PER_CLASS)
//    public class queryGetTopicsBySentenceQuestion{
//        @BeforeAll
//        public void init(){
//            suffix = "/questions/get-topics-sentences";
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
//        /**
//         * t1 -> sentence
//         * t2 -> sentence
//         * t3  -> sentence
//         * t4 ~ -> x
//         * 총 3개의 문제가 생성되어야함
//         */
//        @DisplayName("문장 보고 주제 맞추기 문제 제공 성공")
//        @Test
//        public void queryGetTopicsBySentencesQuestionSuccess(){
//            int num = ch1.getNumber();
//            ResponseEntity<List<QuestionDto>> response = restTemplate
//                    .withBasicAuth("customer1", "customer1")
//                    .exchange(URL + "?num=" + num, HttpMethod.GET, null, new ParameterizedTypeReference<List<QuestionDto>>() {
//                    });
//
//            List<QuestionDto> questionDtoList = response.getBody();
//            //총 3개의 문제가 생성되었는지 확인
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//            assertThat(questionDtoList.size()).isEqualTo(3); // t1,t2,t3각각 1개씩의 문제 생성
//
//            for (QuestionDto questionDto : questionDtoList) {
//                String topicTitle = questionDto.getAnswer();
//
//                //보기로 제시되는 문장이 answer 토픽 내에 존재하는지 확인
//                String descriptionSentence = questionDto.getDescriptionSentence();
//                assertThat(sentenceRepository.querySentenceByContentInTopic(descriptionSentence, topicTitle).isPresent()).isTrue();
//
//                //총 선지가 GET_TOPIC_WRONG_ANSWER_NUM + 1개인지 확인
//                List<QuestionChoiceDto> choiceList = questionDto.getChoiceList();
//                assertThat(choiceList.size()).isEqualTo(GET_TOPIC_WRONG_ANSWER_NUM + 1);
//
//                //정답 주제 1개, 오답 주제 GET_TOPIC_WRONG_ANSWER_NUM개인지 확인
//                int answerCnt = 0;
//                int wrongAnswerCnt = 0;
//                for (QuestionChoiceDto questionChoiceDto : choiceList) {
//                    if(questionChoiceDto.getKey().equals(topicTitle)){
//                        answerCnt++;
//                    }else{
//                        wrongAnswerCnt++;
//                    }
//                }
//
//                assertThat(answerCnt).isEqualTo(1);
//                assertThat(wrongAnswerCnt).isEqualTo(GET_TOPIC_WRONG_ANSWER_NUM);
//            }
//        }
//
//        @DisplayName("문장 보고 주제 맞추기 문제 제공 실패 - 존재하지 않는 단원 번호 입력")
//        @Test
//        public void queryGetTopicsBySentencesQuestionFailWrongNum(){
//            ResponseEntity<List<ErrorMsgDto>> response = restTemplate
//                    .withBasicAuth("customer1", "customer1")
//                    .exchange(URL + "?num=123123123", HttpMethod.GET,
//                    null, new ParameterizedTypeReference<List<ErrorMsgDto>>() {});
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto(CHAPTER_NOT_FOUND.getErrorMessage())));
//
//        }
//    }
//}
