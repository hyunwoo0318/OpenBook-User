package Project.OpenBook.Controller;

import Project.OpenBook.Constants.ErrorCode;
import Project.OpenBook.Constants.ProgressConst;
import Project.OpenBook.Constants.QuestionConst;
import Project.OpenBook.Constants.Role;
import Project.OpenBook.Domain.*;
import Project.OpenBook.Dto.Sentence.SentenceWithTopicDto;
import Project.OpenBook.Dto.choice.ChoiceContentIdDto;
import Project.OpenBook.Dto.error.ErrorMsgDto;
import Project.OpenBook.Dto.keyword.KeywordNameCommentDto;
import Project.OpenBook.Dto.keyword.KeywordWithTopicDto;
import Project.OpenBook.Dto.question.*;
import Project.OpenBook.Repository.category.CategoryRepository;
import Project.OpenBook.Repository.chapter.ChapterRepository;
import Project.OpenBook.Repository.chapterprogress.ChapterProgressRepository;
import Project.OpenBook.Repository.choice.ChoiceRepository;
import Project.OpenBook.Repository.customer.CustomerRepository;
import Project.OpenBook.Repository.description.DescriptionRepository;
import Project.OpenBook.Repository.keyword.KeywordRepository;
import Project.OpenBook.Repository.primarydate.PrimaryDateRepository;
import Project.OpenBook.Repository.sentence.SentenceRepository;
import Project.OpenBook.Repository.topic.TopicRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import springfox.documentation.annotations.ApiIgnore;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

import static Project.OpenBook.Constants.ErrorCode.CHAPTER_NOT_FOUND;
import static Project.OpenBook.Constants.ErrorCode.TOPIC_NOT_FOUND;
import static Project.OpenBook.Constants.QuestionConst.WRONG_ANSWER_NUM;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@TestPropertySource(properties = {  "spring.config.location=classpath:application-test.yml",
//                                    "spring.config.location=classpath:prompt-template.yml"})
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//class QuestionControllerTest {
//
//    @LocalServerPort
//    int port;
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
//    DescriptionRepository descriptionRepository;
//
//    private final String prefix = "http://localhost:";
//
//    String URL;
//
//
//    @BeforeAll
//    public void initTestForChoiceController() {
//        URL = prefix + port;
//        restTemplate = restTemplate.withBasicAuth("admin1", "admin1");
//        restTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
//        init();
//    }
//
//    private void init(){
//
//        //카테고리 전체 저장
//        Category c1 = new Category("유물");
//        Category c2 = new Category("사건");
//        Category c3 = new Category("국가");
//        Category c4 = new Category("인물");
//
//        categoryRepository.saveAllAndFlush(Arrays.asList(c1, c2, c3, c4));
//
//        //단원 전체 저장
//        Chapter ch1 = new Chapter("ch1", 1);
//        Chapter ch2 = new Chapter("ch2", 2);
//        Chapter ch3 = new Chapter("ch3", 3);
//
//        chapterRepository.saveAllAndFlush(Arrays.asList(ch1, ch2, ch3));
//
//        //topic 전체 생성
//        Random rand = new Random();
//        List<Topic> topicList = new ArrayList<>();
//
//
//        for (int i = 1; i <= 1000; i++) {
//            int year = rand.nextInt(2000) + 1;
//            int month = rand.nextInt(12) + 1; // 1~12 사이의 월을 랜덤으로 생성
//            int day = rand.nextInt(26) + 1; // 1부터 최대 일수 사이의 일을 랜덤으로 생성
//            int length = rand.nextInt(500);
//
//            Integer startDate = year * 1000 + month * 100 + day;
//            Integer endDate = startDate + length;
//
//            Category c  = null;
//
//            if(i <= 10){
//                c = c1;
//            }else if(i <= 20){
//                c = c2;
//            } else if (i <= 30) {
//                c = c3;
//            }else {
//                c = c4;
//            }
//
//            Topic topic = new Topic("topic" + i, startDate, endDate, 0, 0, "detail" + i, ch1, c);
//            topicList.add(topic);
//        }
//
//        topicRepository.saveAllAndFlush(topicList);
//
//        //선지, 보기 생성
//        for (Topic topic : topicList) {
//            for (int i = 1; i <= 5; i++) {
//                choiceRepository.save(new Choice("choice" + i + " in " + topic.getTitle(), topic));
//                descriptionRepository.save(new Description("description" + i + " in " + topic.getTitle(), topic));
//            }
//        }
//    }
//
//    @DisplayName("type1 question 생성 성공 - GET /admin/temp-question?type=1&category=사건")
//    @Test
//    public void type1QuestionSuccess(){
//        Long type = 1L;
//        String categoryName = "사건";
//        String prompt = "해당 사건에 대한 설명으로 옳은 것은?";
//
//        List<Category> all = categoryRepository.findAll();
//
//
//        ResponseEntity<QuestionDto> response = restTemplate.getForEntity(URL + "/admin/temp-question?type=1&category=사건", QuestionDto.class);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//
//        QuestionDto questionDto = response.getBody();
//
//        assertThat(questionDto.getType()).isEqualTo(type);
//        assertThat(questionDto.getCategoryName()).isEqualTo(categoryName);
//        assertThat(questionDto.getPrompt()).isEqualTo(prompt);
//
//        //정답 선지와 보기의 topic이 동일한지 확인
//        Long descriptionId = questionDto.getDescription().getId();
//        Optional<Description> descriptionOptional = descriptionRepository.findById(descriptionId);
//        assertThat(descriptionOptional.isEmpty()).isFalse();
//        Long descriptionTopicId = descriptionOptional.get().getTopic().getId();
//
//        Long answerChoiceId = questionDto.getAnswerChoiceId();
//        Optional<Choice> choiceOptional = choiceRepository.findById(answerChoiceId);
//        assertThat(choiceOptional.isEmpty()).isFalse();
//        Long choiceTopicId = choiceOptional.get().getTopic().getId();
//
//        assertThat(descriptionTopicId).isEqualTo(choiceTopicId);
//
//        //총 선지가 5개 들어갔는지 테스트
//        List<Long> choiceIdList = questionDto.getChoiceList().stream().map(q -> q.getId()).collect(Collectors.toList());
//        List<Choice> choiceList = choiceRepository.findAllById(choiceIdList);
//        assertThat(choiceList.size()).isEqualTo(5);
//
//        //선지 5개의 topic이 모두 category가 요구사항과 맞는지 테스트
//
//
//        //오답 선지들은 보기와 topic이 다른지 테스트
//        for (Choice choice : choiceList) {
//            Category category = choiceRepository.queryCategoryByChoice(choice.getId());
//            assertThat(category.getName()).isEqualTo(categoryName);
//            if(choice.getId() == questionDto.getAnswerChoiceId()){
//                continue;
//            }else{
//                assertThat(choice.getTopic().getId()).isNotEqualTo(descriptionTopicId);
//            }
//        }
//    }
//
//    @DisplayName("type2 question 생성 성공 -  GET /admin/temp-question?type=2&category=사건")
//    @Test
//    public void type2QuestionSuccess() {
//        Long type = 2L;
//        String categoryName = "사건";
//        String prompt = "해당 사건이 발생한 시기에 동아시아에서 볼 수 있는 모습으로 가장 적절한 것은?";
//
//        ResponseEntity<QuestionDto> response = restTemplate.getForEntity(URL + "/admin/temp-question?type=2&category=사건", QuestionDto.class);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//
//        QuestionDto questionDto = response.getBody();
//
//        assertThat(questionDto.getType()).isEqualTo(type);
//        assertThat(questionDto.getCategoryName()).isEqualTo(categoryName);
//        assertThat(questionDto.getPrompt()).isEqualTo(prompt);
//
//        //보기의 topic의 시간대의 사이에 정답 선지의 시간대가 있는지 테스트
//        Long descriptionId = questionDto.getDescription().getId();
//        Topic descriptionTopic = topicRepository.queryTopicByDescription(descriptionId);
//        Topic answerTopic = topicRepository.queryTopicByChoice(questionDto.getAnswerChoiceId());
//
//        assertThat(answerTopic.getEndDate()>=(descriptionTopic.getEndDate())).isTrue();
//        assertThat(answerTopic.getStartDate()<=(descriptionTopic.getStartDate())).isTrue();
//
//        //오답 선지들은 해당 범위에 없는지 테스트
//        List<ChoiceContentIdDto> choiceList = questionDto.getChoiceList();
//        for (ChoiceContentIdDto c : choiceList) {
//            if(c.getId() == questionDto.getAnswerChoiceId()) continue;
//            else{
//                Topic topic = topicRepository.queryTopicByChoice(c.getId());
//                assertThat((topic.getEndDate()<(descriptionTopic.getEndDate())) &&
//                        (topic.getStartDate()>(descriptionTopic.getStartDate()))).isFalse();
//            }
//        }
//    }
//
//    @DisplayName("type3 question 생성 성공 -  GET /admin/temp-question?type=3&category=사건")
//    @Test
//    public void type3QuestionSuccess() {
//        Long type = 3L;
//        String categoryName = "사건";
//        String prompt = "해당 사건이 발생한 이후에 동아시아에서 볼 수 있는 모습으로 가장 적절한 것은?";
//
//        ResponseEntity<QuestionDto> response = restTemplate.getForEntity(URL + "/admin/temp-question?type=3&category=사건", QuestionDto.class);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//
//        QuestionDto questionDto = response.getBody();
//
//        assertThat(questionDto.getType()).isEqualTo(type);
//        assertThat(questionDto.getCategoryName()).isEqualTo(categoryName);
//        assertThat(questionDto.getPrompt()).isEqualTo(prompt);
//
//        //보기의 topic의 시간대의 사이에 정답 선지의 시간대가 있는지 테스트
//        Long descriptionId = questionDto.getDescription().getId();
//        Topic descriptionTopic = topicRepository.queryTopicByDescription(descriptionId);
//        Topic answerTopic = topicRepository.queryTopicByChoice(questionDto.getAnswerChoiceId());
//
//        assertThat(answerTopic.getStartDate()>(descriptionTopic.getEndDate())).isTrue();
//
//        //오답 선지들은 해당 범위에 없는지 테스트
//        List<ChoiceContentIdDto> choiceList = questionDto.getChoiceList();
//        for (ChoiceContentIdDto c : choiceList) {
//            if(c.getId() == questionDto.getAnswerChoiceId()) continue;
//            else{
//                Topic topic = topicRepository.queryTopicByChoice(c.getId());
//                assertThat(topic.getStartDate()>(descriptionTopic.getEndDate())).isFalse();
//            }
//        }
//    }
//
//    @DisplayName("type4 question 생성 성공 -  GET /admin/temp-question?type=4&category=사건")
//    @Test
//    public void type4QuestionSuccess() {
//        Long type = 4L;
//        String categoryName = "사건";
//        String prompt = "해당 사건이 발생한 이전에 동아시아에서 볼 수 있는 모습으로 가장 적절한 것은?";
//
//        ResponseEntity<QuestionDto> response = restTemplate.getForEntity(URL + "/admin/temp-question?type=4&category=사건", QuestionDto.class);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//
//        QuestionDto questionDto = response.getBody();
//
//        assertThat(questionDto.getType()).isEqualTo(type);
//        assertThat(questionDto.getCategoryName()).isEqualTo(categoryName);
//        assertThat(questionDto.getPrompt()).isEqualTo(prompt);
//
//        //보기의 topic의 시간대의 사이에 정답 선지의 시간대가 있는지 테스트
//        Long descriptionId = questionDto.getDescription().getId();
//        Topic descriptionTopic = topicRepository.queryTopicByDescription(descriptionId);
//        Topic answerTopic = topicRepository.queryTopicByChoice(questionDto.getAnswerChoiceId());
//
//        assertThat(answerTopic.getEndDate()<(descriptionTopic.getStartDate())).isTrue();
//
//        //오답 선지들은 해당 범위에 없는지 테스트
//        List<ChoiceContentIdDto> choiceList = questionDto.getChoiceList();
//        for (ChoiceContentIdDto c : choiceList) {
//            if(c.getId() == questionDto.getAnswerChoiceId()) continue;
//            else{
//                Topic topic = topicRepository.queryTopicByChoice(c.getId());
//                assertThat(topic.getEndDate()<(descriptionTopic.getStartDate())).isFalse();
//            }
//        }
//    }
//
////    @DisplayName("type5 question 생성 성공 -  GET /admin/temp-question?type=5&category=사건")
////    @Test
////    public void type5QuestionSuccess() {
////        Long type = 5L;
////        String categoryName = "사건";
////        String prompt = "해당 사건이 발생한 시기를 연표에서 옳게 고른 것은?";
////
////        ResponseEntity<QuestionDto> response = restTemplate.getForEntity(URL + "/admin/temp-question?type=5&category=사건", QuestionDto.class);
////        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
////
////        QuestionDto questionDto = response.getBody();
////
////        assertThat(questionDto.getType()).isEqualTo(type);
////        assertThat(questionDto.getCategoryName()).isEqualTo(categoryName);
////        assertThat(questionDto.getPrompt()).isEqualTo(prompt);
////    }
//
//
//
//}


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = { "spring.config.location=classpath:application-test.yml" })
class QuestionControllerTest{

    @LocalServerPort
    int port;
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private ChapterRepository chapterRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    PrimaryDateRepository primaryDateRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    KeywordRepository keywordRepository;
    @Autowired
    SentenceRepository sentenceRepository;

    @Autowired
    ChapterProgressRepository chapterProgressRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    TestRestTemplate restTemplate;

    private final String prefix = "http://localhost:";
    private String suffix;
    private String URL;

    private Customer customer1, customer2;

    private Topic t1,t2,t3;

    private Chapter ch1;
    private Category c1;

    private PrimaryDate date1, date2;
    private Keyword k1,k2,k3,k4,k5,k6;
    private Sentence s1,s2,s3,s4,s5,s6;

    private void initConfig() {
        URL = prefix + port + suffix;
    }

    private void baseSetting() {

        customer1 = new Customer("customer1", passwordEncoder.encode("customer1"), Role.USER);
        customer2 = new Customer("customer2", passwordEncoder.encode("customer2"), Role.USER);
        customerRepository.saveAllAndFlush(Arrays.asList(customer1, customer2));

        c1 = new Category("c1");
        categoryRepository.saveAndFlush(c1);

        ch1 = new Chapter("ch1", 1);
        chapterRepository.saveAndFlush(ch1);

        ChapterProgress chapterProgress = new ChapterProgress(customer1, ch1);
        chapterProgressRepository.saveAndFlush(chapterProgress);

        List<Topic> topicList = new ArrayList<>();
        t1 = new Topic("title1", 100, 200, true,false,0, 0, "detail1", ch1, c1);
        t2 = new Topic("title2", 300, 400, false, true, 0, 0, "detail2", ch1, c1);
        t3 = new Topic("title3", 500, 600, false, false, 0, 0, "detail3", ch1, c1);
        topicList.addAll(Arrays.asList(t1, t2, t3));

        //추가적으로 토픽 20개 더 추가
        for (int i = 4; i < 24; i++) {
            Topic topic = new Topic("title" + i,0,0,false,false,0,0,"detail" + i, ch1, c1);
            topicList.add(topic);
        }
        topicRepository.saveAllAndFlush(topicList);

        date1 = new PrimaryDate(13330111, true, "comment1", t1);
        date2 = new PrimaryDate(13330101, false, "comment2", t1);
        primaryDateRepository.saveAllAndFlush(Arrays.asList(date1, date2));

        /**
         * t1 -> k1,k2, s1,s2
         * t2 -> k3,k4,k5 s3,s4,s5
         * t3 -> k6, s6
         */

        k1 = new Keyword("k1", "c1", t1,null);
        k2 = new Keyword("k2", "c2", t1,null);
        k3 = new Keyword("k3", "c3", t2,null);
        k4 = new Keyword("k4", "c4", t2,null);
        k5 = new Keyword("k5", "c5", t2,null);
        k6 = new Keyword("k6", "c6", t3,null);
        keywordRepository.saveAllAndFlush(Arrays.asList(k1, k2, k3, k4, k5, k6));

        s1 = new Sentence("s1", t1);
        s2 = new Sentence("s2", t1);
        s3 = new Sentence("s3", t2);
        s4 = new Sentence("s4", t2);
        s5 = new Sentence("s5", t2);
        s6 = new Sentence("s6", t3);
        sentenceRepository.saveAllAndFlush(Arrays.asList(s1, s2, s3, s4, s5, s6));

    }

    private void baseClear() {
        chapterProgressRepository.deleteAllInBatch();
        customerRepository.deleteAllInBatch();
        sentenceRepository.deleteAllInBatch();
        keywordRepository.deleteAllInBatch();
        primaryDateRepository.deleteAllInBatch();
        topicRepository.deleteAllInBatch();
        chapterRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
    }

    @Nested
    @DisplayName("연표 제공 - GET /questions/time-flow")
    @TestInstance(PER_CLASS)
    public class queryTimeFlowQuestion{
        @BeforeAll
        public void init(){
            suffix = "/questions/time-flow";
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

        /**
         * expectResult - [t1.startDate, t2.endDate, date1]
         */
        @DisplayName("연표 문제 제공 성공")
        @Test
        public void queryTimeFlowQuestion(){
            TimeFlowQuestionDto dto1 = new TimeFlowQuestionDto(t1.getStartDate(), t1.getTitle() + "의 시작연도입니다.", t1.getTitle());
            TimeFlowQuestionDto dto2 = new TimeFlowQuestionDto(t2.getEndDate(), t2.getTitle() + "의 종료연도입니다.", t2.getTitle());
            TimeFlowQuestionDto dto3 = new TimeFlowQuestionDto(date1.getExtraDate(), date1.getExtraDateComment(), t1.getTitle());

            ResponseEntity<List<TimeFlowQuestionDto>> response = restTemplate
                    .withBasicAuth("customer1", "customer1")
                    .exchange(URL + "?num=1&type=Question", HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<TimeFlowQuestionDto>>() {});

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

            List<TimeFlowQuestionDto> body = response.getBody();
            assertThat(body.size()).isEqualTo(3);
            assertThat(body).usingRecursiveComparison().isEqualTo(Arrays.asList(dto1, dto2, dto3));

            //process update test
            ChapterProgress chapterProgress = chapterProgressRepository.queryChapterProgress(customer1.getId(), ch1.getNumber()).orElseThrow();
            Assertions.assertThat(chapterProgress.getProgress()).isEqualTo(ProgressConst.TIME_FLOW_QUESTION);
        }

        @DisplayName("연표 학습 제공 성공")
        @Test
        public void queryTimeFlowStudy(){
            TimeFlowQuestionDto dto1 = new TimeFlowQuestionDto(t1.getStartDate(), t1.getTitle() + "의 시작연도입니다.", t1.getTitle());
            TimeFlowQuestionDto dto2 = new TimeFlowQuestionDto(t2.getEndDate(), t2.getTitle() + "의 종료연도입니다.", t2.getTitle());
            TimeFlowQuestionDto dto3 = new TimeFlowQuestionDto(date1.getExtraDate(), date1.getExtraDateComment(), t1.getTitle());

            ResponseEntity<List<TimeFlowQuestionDto>> response = restTemplate
                    .withBasicAuth("customer1", "customer1")
                    .exchange(URL + "?num=1&type=Study", HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<TimeFlowQuestionDto>>() {});

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

            List<TimeFlowQuestionDto> body = response.getBody();
            assertThat(body.size()).isEqualTo(3);
            assertThat(body).usingRecursiveComparison().isEqualTo(Arrays.asList(dto1, dto2, dto3));

            //process update test
            ChapterProgress chapterProgress = chapterProgressRepository.queryChapterProgress(customer1.getId(), ch1.getNumber()).orElseThrow();
            Assertions.assertThat(chapterProgress.getProgress()).isEqualTo(ProgressConst.TIME_FLOW_STUDY);
        }

        @DisplayName("연표 제공 실패 - 존재하지 않는 단원번호 입력")
        @Test
        public void queryTimeFlowFail() {
            ResponseEntity<List<ErrorMsgDto>> response = restTemplate
                    .withBasicAuth("customer1", "customer1")
                    .exchange(URL + "?num=-1&type=Question", HttpMethod.GET,
                    null, new ParameterizedTypeReference<List<ErrorMsgDto>>() {});

            Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            Assertions.assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto(ErrorCode.CHAPTER_NOT_FOUND.getErrorMessage())));

            //process update test
            ChapterProgress chapterProgress = chapterProgressRepository.queryChapterProgress(customer1.getId(), ch1.getNumber()).orElseThrow();
            Assertions.assertThat(chapterProgress.getProgress()).isEqualTo(ProgressConst.NOT_STARTED);
        }
    }

    @Nested
    @DisplayName("주제보고 키워드 맞추기 문제 제공 - GET /questions/get-keywords")
    @TestInstance(PER_CLASS)
    public class queryGetKeywordsQuestion{
        @BeforeAll
        public void init(){
            suffix = "/questions/get-keywords";
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

        @DisplayName("주제보고 키워드/문장 맞추기 문제 제공 성공")
        @Test
        public void queryGetKeywordsQuestionSuccess(){
            String topicTitle = t3.getTitle();
            ResponseEntity<GetKeywordQuestionDto> response = restTemplate.getForEntity(URL + "?title=" + topicTitle, GetKeywordQuestionDto.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

            GetKeywordQuestionDto body = response.getBody();

            List<KeywordNameCommentDto> answerKeywordList = body.getAnswerKeywordList();
            List<KeywordWithTopicDto> wrongAnswerKeywordList = body.getWrongAnswerKeywordList();


            //정답주제의 모든 키워드를 가지고 있는지 확인
            List<Keyword> expectKeywordList = keywordRepository.queryKeywordsByTopic(topicTitle);
            assertThat(answerKeywordList.size()).isEqualTo(expectKeywordList.size());

            //정답 키워드이 올바른지 확인
            Set<String> keywordNameSet = expectKeywordList.stream().map(k -> k.getName()).collect(Collectors.toSet());
            for (KeywordNameCommentDto dto : answerKeywordList) {
                assertThat(keywordNameSet.contains(dto.getName()));
            }

            //오답 키워드의 개수가 정해진 수인지 확인
            assertThat(wrongAnswerKeywordList.size()).isEqualTo(answerKeywordList.size() * WRONG_ANSWER_NUM);

            //오답 키워드가 정답 주제의 키워드가 맞는지 아닌지 확인
            for (KeywordWithTopicDto dto : wrongAnswerKeywordList) {
                assertThat(dto.getTopicTitle()).isNotEqualTo(topicTitle);
            }

        }

        @DisplayName("주제 보고 키워드 맞추기 문제 제공 성공 - 키워드가 부족한 경우")
        @Test
        public void queryGetKeywordsQuestionSuccessLessKeywordsSentence(){
            String topicTitle = t2.getTitle();
            ResponseEntity<GetKeywordQuestionDto> response = restTemplate.getForEntity(URL + "?title=" + topicTitle, GetKeywordQuestionDto.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

            GetKeywordQuestionDto body = response.getBody();

            List<KeywordNameCommentDto> answerKeywordList = body.getAnswerKeywordList();
            List<KeywordWithTopicDto> wrongAnswerKeywordList = body.getWrongAnswerKeywordList();


            //정답주제의 모든 키워드를 가지고 있는지 확인
            List<Keyword> expectKeywordList = keywordRepository.queryKeywordsByTopic(topicTitle);
            assertThat(answerKeywordList.size()).isEqualTo(expectKeywordList.size());

            //정답 키워드가 올바른지 확인
            Set<String> keywordNameSet = expectKeywordList.stream().map(k -> k.getName()).collect(Collectors.toSet());
            for (KeywordNameCommentDto dto : answerKeywordList) {
                assertThat(keywordNameSet.contains(dto.getName()));
            }

            //오답 키워드, 문장이 정답 주제의 키워드,문장이 아닌지 확인
            for (KeywordWithTopicDto dto : wrongAnswerKeywordList) {
                assertThat(dto.getTopicTitle()).isNotEqualTo(topicTitle);
            }
        }

        @DisplayName("주제보고 키워드 맞추기 문제 제공 실패 - 존재하지 않는 토픽 제목 입력")
        @Test
        public void queryGetKeywordsQuestionFailWrongTitle(){
            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL + "?title=title99999", HttpMethod.GET,
                    null, new ParameterizedTypeReference<List<ErrorMsgDto>>() {});

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto(TOPIC_NOT_FOUND.getErrorMessage())));
        }



    }

    @Nested
    @DisplayName("주제보고 문장 맞추기 문제 제공 - GET /questions/get-sentences")
    @TestInstance(PER_CLASS)
    public class queryGetSentencesQuestion{
        @BeforeAll
        public void init(){
            suffix = "/questions/get-sentences";
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

        @DisplayName("주제보고 문장 맞추기 문제 제공 성공")
        @Test
        public void queryGetSentencesQuestionSuccess(){
            String topicTitle = t3.getTitle();
            ResponseEntity<GetSentenceQuestionDto> response = restTemplate.getForEntity(URL + "?title=" + topicTitle, GetSentenceQuestionDto.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

            GetSentenceQuestionDto body = response.getBody();

            List<String> answerSentenceList = body.getAnswerSentenceList();
            List<SentenceWithTopicDto> wrongAnswerSentenceList = body.getWrongAnswerSentenceList();

            //정답주제의 모든 문장을 가지고 있는지 확인
            List<Sentence> expectSentenceList = sentenceRepository.queryByTopicTitle(topicTitle);
            assertThat(answerSentenceList.size()).isEqualTo(expectSentenceList.size());

            //정답 문장이 올바른지 확인
            Set<String> sentenceNameSet = expectSentenceList.stream().map(s -> s.getName()).collect(Collectors.toSet());

            for (String s : answerSentenceList) {
                assertThat(sentenceNameSet.contains(s));
            }


            //오답 문장의 개수가 정해진 수인지 확인
            assertThat(wrongAnswerSentenceList.size()).isEqualTo(answerSentenceList.size() * WRONG_ANSWER_NUM);

            //오답 문장이 정답 주제의 문장이 아닌지 확인
            for (SentenceWithTopicDto dto : wrongAnswerSentenceList) {
                assertThat(dto.getTopicTitle()).isNotEqualTo(topicTitle);
            }

        }

        @DisplayName("주제 보고 문장 맞추기 문제 제공 성공 - 문장수가 부족한 경우")
        @Test
        public void queryGetSentencesQuestionSuccessLessKeywordsSentence(){
            String topicTitle = t2.getTitle();
            ResponseEntity<GetSentenceQuestionDto> response = restTemplate.getForEntity(URL + "?title=" + topicTitle, GetSentenceQuestionDto.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

            GetSentenceQuestionDto body = response.getBody();

            List<String> answerSentenceList = body.getAnswerSentenceList();
            List<SentenceWithTopicDto> wrongAnswerSentenceList = body.getWrongAnswerSentenceList();

            //정답주제의 모든 문장을 가지고 있는지 확인
            List<Sentence> expectSentenceList = sentenceRepository.queryByTopicTitle(topicTitle);
            assertThat(answerSentenceList.size()).isEqualTo(expectSentenceList.size());

            //정답 문장이 올바른지 확인
            Set<String> sentenceNameSet = expectSentenceList.stream().map(s -> s.getName()).collect(Collectors.toSet());

            for (String s : answerSentenceList) {
                assertThat(sentenceNameSet.contains(s));
            }


            //오답 문장이 정답 주제의 문장이 아닌지 확인
            for (SentenceWithTopicDto dto : wrongAnswerSentenceList) {
                assertThat(dto.getTopicTitle()).isNotEqualTo(topicTitle);
            }
        }

        @DisplayName("주제보고 문장 맞추기 문제 제공 실패 - 존재하지 않는 토픽 제목 입력")
        @Test
        public void queryGeSentencesQuestionFailWrongTitle(){
            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL + "?title=title99999", HttpMethod.GET,
                    null, new ParameterizedTypeReference<List<ErrorMsgDto>>() {});

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto(TOPIC_NOT_FOUND.getErrorMessage())));
        }



    }

    @Nested
    @DisplayName("키워드 보고 주제 맞추기 문제 제공 - GET /questions/get-topics-keywords")
    @TestInstance(PER_CLASS)
    public class queryGetTopicsByKeywordQuestion{
        @BeforeAll
        public void init(){
            suffix = "/questions/get-topics-keywords";
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

        /**
         * t1 -> keyword
         * t2 -> keyword
         * t3 ~ -> x
         * 총 2개의 문제가 생성되어야함
         */
        @DisplayName("키워드/문장 보고 주제 맞추기 문제 제공 성공")
        @Test
        public void queryGetTopicsByKeywordQuestionSuccess(){
            int num = ch1.getNumber();

            ResponseEntity<List<GetTopicByKeywordQuestionDto>> response = restTemplate
                    .withBasicAuth("customer1","customer1")
                    .exchange(URL + "?num=" + num, HttpMethod.GET,
                    null, new ParameterizedTypeReference<List<GetTopicByKeywordQuestionDto>>() {});

            List<GetTopicByKeywordQuestionDto> questionDtoList = response.getBody();
            //총 2개의 문제가 생성되었는지 확인
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(questionDtoList.size()).isEqualTo(2);

            for (GetTopicByKeywordQuestionDto dto : questionDtoList) {
                //정답 주제가 t1,t2내에서 생성되었는지 확인
                assertThat(Arrays.asList(t1.getTitle(), t2.getTitle(), t3.getTitle()).contains(dto.getAnswer())).isTrue();



                //문제에서 제시할 키워드의 개수가 맞는지 확인
                assertThat(dto.getAnswerKeywordList().size()).isEqualTo(2);

                //키워드들이 정답 주제 내의 키워드가 맞는지 확인
                List<KeywordNameCommentDto> keywordDtoList = keywordRepository.queryKeywordsByTopic(dto.getAnswer()).stream()
                                                                .map(d -> new KeywordNameCommentDto(d.getName(), d.getComment()))
                                                                .collect(Collectors.toList());
                assertThat(keywordDtoList.containsAll(dto.getAnswerKeywordList()));

                //오답 주제의 개수가 8개인지 확인
                assertThat(dto.getWrongAnswerList().size()).isEqualTo(8);
                //각각의 오답 주제가 정답 주제와 겹치지 않는지 확인
                assertThat(dto.getWrongAnswerList().contains(dto.getAnswer())).isFalse();
            }

            //process update test
            ChapterProgress chapterProgress = chapterProgressRepository.queryChapterProgress(customer1.getId(), ch1.getNumber()).orElseThrow();
            Assertions.assertThat(chapterProgress.getProgress()).isEqualTo(ProgressConst.GET_TOPIC_BY_KEYWORD);
        }

        @DisplayName("키워드 보고 주제 맞추기 문제 제공 실패 - 존재하지 않는 단원 번호 입력")
        @Test
        public void queryGetTopicsByKeywordQuestionFailWrongNum(){
            ResponseEntity<List<ErrorMsgDto>> response = restTemplate
                    .withBasicAuth("customer1", "customer1")

                    .exchange(URL + "?num=123123123", HttpMethod.GET,
                    null, new ParameterizedTypeReference<List<ErrorMsgDto>>() {});

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto(CHAPTER_NOT_FOUND.getErrorMessage())));

            //process update test
            ChapterProgress chapterProgress = chapterProgressRepository.queryChapterProgress(customer1.getId(), ch1.getNumber()).orElseThrow();
            Assertions.assertThat(chapterProgress.getProgress()).isEqualTo(ProgressConst.NOT_STARTED);
        }
    }

    @Nested
    @DisplayName("문장 보고 주제 맞추기 문제 제공 - GET /questions/get-topics-sentences")
    @TestInstance(PER_CLASS)
    public class queryGetTopicsBySentenceQuestion{
        @BeforeAll
        public void init(){
            suffix = "/questions/get-topics-sentences";
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

        /**
         * t1 -> sentence
         * t2 -> sentence
         * t3  -> sentence
         * t4 ~ -> x
         * 총 3개의 문제가 생성되어야함
         */
        @DisplayName("문장 보고 주제 맞추기 문제 제공 성공")
        @Test
        public void queryGetTopicsBySentencesQuestionSuccess(){
            int num = ch1.getNumber();

            ResponseEntity<List<GetTopicBySentenceQuestionDto>> response = restTemplate
                    .withBasicAuth("customer1", "customer1")
                    .exchange(URL + "?num=" + num, HttpMethod.GET,
                    null, new ParameterizedTypeReference<List<GetTopicBySentenceQuestionDto>>() {});

            List<GetTopicBySentenceQuestionDto> questionDtoList = response.getBody();
            //총 3개의 문제가 생성되었는지 확인
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(questionDtoList.size()).isEqualTo(3);

            for (GetTopicBySentenceQuestionDto dto : questionDtoList) {
                //정답 주제가 t1,t2내에서 생성되었는지 확인
                assertThat(Arrays.asList(t1.getTitle(), t2.getTitle(), t3.getTitle()).contains(dto.getAnswer())).isTrue();


                //문장이 정답 주제 내의 문장이 맞는지 확인
                List<String> sentenceList = sentenceRepository.queryByTopicTitle(dto.getAnswer()).stream()
                            .map(d -> d.getName())
                            .collect(Collectors.toList());
                assertThat(sentenceList.contains(dto.getSentence()));

                //오답 주제의 개수가 8개인지 확인
                assertThat(dto.getWrongAnswerList().size()).isEqualTo(8);
                //각각의 오답 주제가 정답 주제와 겹치지 않는지 확인
                assertThat(dto.getWrongAnswerList().contains(dto.getAnswer())).isFalse();
            }

            //process update test
            ChapterProgress chapterProgress = chapterProgressRepository.queryChapterProgress(customer1.getId(), ch1.getNumber()).orElseThrow();
            Assertions.assertThat(chapterProgress.getProgress()).isEqualTo(ProgressConst.GET_TOPIC_BY_SENTENCE);
        }

        @DisplayName("문장 보고 주제 맞추기 문제 제공 실패 - 존재하지 않는 단원 번호 입력")
        @Test
        public void queryGetTopicsBySentencesQuestionFailWrongNum(){
            ResponseEntity<List<ErrorMsgDto>> response = restTemplate
                    .withBasicAuth("customer1", "customer1")
                    .exchange(URL + "?num=123123123", HttpMethod.GET,
                    null, new ParameterizedTypeReference<List<ErrorMsgDto>>() {});

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto(CHAPTER_NOT_FOUND.getErrorMessage())));

            //process update test
            ChapterProgress chapterProgress = chapterProgressRepository.queryChapterProgress(customer1.getId(), ch1.getNumber()).orElseThrow();
            Assertions.assertThat(chapterProgress.getProgress()).isEqualTo(ProgressConst.NOT_STARTED);
        }
    }
}
