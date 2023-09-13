package Project.OpenBook.Controller;//package Project.OpenBook.Controller;
//
//import Project.OpenBook.Chapter.Domain.Chapter;
//import Project.OpenBook.Domain.*;
//import Project.OpenBook.Dto.choice.*;
//import Project.OpenBook.Dto.error.ErrorMsgDto;
//import Project.OpenBook.Repository.category.CategoryRepository;
//import Project.OpenBook.Chapter.Repo.ChapterRepository;
//import Project.OpenBook.Repository.chaptersection.ChapterSectionRepository;
//import Project.OpenBook.Repository.description.DescriptionRepository;
//import Project.OpenBook.Repository.dupcontent.DupContentRepository;
//import Project.OpenBook.Repository.topic.TopicRepository;
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
//class DupContentControllerTest {
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
//    ChapterSectionRepository chapterSectionRepository;
//
//    @Autowired
//    TopicProgressRepository topicProgressRepository;
//
//    @Autowired
//    CategoryRepository categoryRepository;
//
//    @Autowired
//    TopicRepository topicRepository;
//
//    @Autowired
//    DescriptionRepository descriptionRepository;
//
//    @Autowired private ChoiceRepository choiceRepository;
//
//    @Autowired private DupContentRepository dupContentRepository;
//
//    private final String prefix = "http://localhost:";
//
//    private String URL,suffix;
//
//    Topic topic1, topic2;
//
//    Description desc1, desc2, desc3, desc4, desc5;
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
//        choice1 = new Choice("choice1", topic1);
//        choice2 = new Choice("choice2", topic1);
//        choice3 = new Choice("choice3", topic1);
//
//        choice4 = new Choice("choice4", topic2);
//        choice5 = new Choice("choice5", topic2);
//
//        choiceRepository.saveAllAndFlush(Arrays.asList(choice1, choice2, choice3, choice4, choice5));
//
//        /**
//         * desc1,2,3 -> topic1
//         * desc4,5 -> topic2
//         */
//        desc1 = new Description("desc1", topic1);
//        desc2 = new Description("desc2", topic1);
//        desc3 = new Description("desc3", topic1);
//
//        desc4 = new Description("desc4", topic2);
//        desc5 = new Description("desc5", topic2);
//
//        descriptionRepository.saveAllAndFlush(Arrays.asList(desc1, desc2, desc3, desc4, desc5));
//
//        DupContent dupContent1 = new DupContent(desc1, choice1);
//        DupContent dupContent2 = new DupContent(desc1, choice2);
//        dupContentRepository.saveAllAndFlush(Arrays.asList(dupContent1, dupContent2));
//    }
//    private void baseClear() {
//        dupContentRepository.deleteAllInBatch();
//        choiceRepository.deleteAllInBatch();
//        descriptionRepository.deleteAllInBatch();
//        topicRepository.deleteAllInBatch();
//        chapterRepository.deleteAllInBatch();
//        categoryRepository.deleteAllInBatch();
//    }
//
//    @Nested
//    @DisplayName("특정 보기와 내용이 겹치는 선지 모두 조회 - GET /admin/dup-contents/{descriptionId}")
//    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
//    public class queryDupContentChoices{
//
//        private Long descriptionId;
//        @BeforeAll
//        public void init(){
//            suffix = "/admin/dup-contents/";
//            initConfig();
//        }
//
//        @BeforeEach
//        public void setting(){
//            baseSetting();
//            descriptionId = desc1.getId();
//        }
//
//        @AfterEach
//        public void clear(){
//            baseClear();
//        }
//
//        @DisplayName("특정 보기와 내용이 겹치는 모든 선지 조회")
//        @Test
//        public void queryDupContentChoicesSuccess() {
//            ResponseEntity<List<ChoiceDto>> response = restTemplate.exchange(URL + descriptionId, HttpMethod.GET, null, new ParameterizedTypeReference<List<ChoiceDto>>() {
//            });
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ChoiceDto(choice1), new ChoiceDto(choice2)));
//        }
//
//        @DisplayName("특정 보기와 내용이 겹치는 모든 선지 조회 실패 - 존재하지 않는 보기 id입력")
//        @Test
//        public void queryDupContentChoicesFail(){
//            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL + "123123", HttpMethod.GET, null, new ParameterizedTypeReference<List<ErrorMsgDto>>() {});
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto("존재하지 않는 보기 ID입니다.")));
//        }
//    }
//
//    @Nested
//    @DisplayName("특정 보기와 내용이 겹치는 선지들 선정 - POST /admin/dup-contents/{descriptionId}")
//    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
//    public class createDupContent{
//
//        private Long descriptionId;
//        @BeforeAll
//        public void init(){
//            suffix = "/admin/dup-contents/";
//            initConfig();
//        }
//
//        @BeforeEach
//        public void setting(){
//            baseSetting();
//            descriptionId = desc1.getId();
//        }
//
//        @AfterEach
//        public void clear(){
//            baseClear();
//        }
//
//        @DisplayName("특정 보기와 내용이 겹치는 모든 선지 선택 성공 - 모든 선지가 새로 추가되는 경우")
//        @Test
//        public void createDupContentChoicesSuccessNew() {
//            ChoiceIdListDto dto = new ChoiceIdListDto(Arrays.asList(choice4.getId(), choice5.getId()));
//
//            ResponseEntity<Void> response = restTemplate.postForEntity(URL + descriptionId, dto,Void.class);
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
//            assertThat(dupContentRepository.queryDupContentChoices(descriptionId).size()).isEqualTo(4); // 기존 2 + 새로 추가된 2
//        }
//
//        @DisplayName("특정 보기와 내용이 겹치는 모든 선지 선택 성공 - 이미 추가된 선지도 넘어오는 경우")
//        @Test
//        public void createDupContentChoicesSuccessExist() {
//            ChoiceIdListDto dto = new ChoiceIdListDto(Arrays.asList(choice1.getId(), choice5.getId()));
//
//            ResponseEntity<Void> response = restTemplate.postForEntity(URL + descriptionId, dto,Void.class);
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
//            assertThat(dupContentRepository.queryDupContentChoices(descriptionId).size()).isEqualTo(3); // 기존 2 + 새로 추가된 1
//        }
//
//        @DisplayName("특정 보기와 내용이 겹치는 모든 선지 선택 실패 - DTO Validation")
//        @Test
//        public void createDupContentChoicesFailWrongDto(){
//            ChoiceIdListDto dto = new ChoiceIdListDto();
//
//            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL + descriptionId, HttpMethod.POST,
//                    new HttpEntity<>(dto), new ParameterizedTypeReference<List<ErrorMsgDto>>() {
//            });
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
//            assertThat(response.getBody().size()).isEqualTo(1);
//        }
//
//        @DisplayName("특정 보기와 내용이 겹치는 모든 선지 선택 실패 - 선지 id 일부에 존재하지 않는 id가 포함되어 있는 경우")
//        @Test
//        public void createDupContentChoicesFailNotExistChoice() {
//            ChoiceIdListDto dto = new ChoiceIdListDto(Arrays.asList(choice4.getId(), choice5.getId(), 123123L, 456456L));
//
//            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL + descriptionId, HttpMethod.POST,
//                    new HttpEntity<>(dto), new ParameterizedTypeReference<List<ErrorMsgDto>>() {
//            });
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto("존재하지 않는 선지 ID입니다.")));
//        }
//    }
//
//    @Nested
//    @DisplayName("특정 보기와 내용이 겹치는 선지 삭제 - DELETE /admin/dup-contents/{descriptionId}")
//    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
//    public class deleteDupContent{
//
//        private Long descriptionId;
//        @BeforeAll
//        public void init(){
//            suffix = "/admin/dup-contents/";
//            initConfig();
//        }
//
//        @BeforeEach
//        public void setting(){
//            baseSetting();
//            descriptionId = desc1.getId();
//        }
//
//        @AfterEach
//        public void clear(){
//            baseClear();
//        }
//
//        @DisplayName("특정 보기와 내용이 겹치는 선지 삭제 성공")
//        @Test
//        public void deleteDupContentChoicesSuccess() {
//            ChoiceIdDto dto = new ChoiceIdDto(choice1.getId());
//            ResponseEntity<Void> response = restTemplate.exchange(URL + descriptionId, HttpMethod.DELETE, new HttpEntity<>(dto), Void.class);
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//            assertThat(dupContentRepository.queryDupContentChoices(descriptionId).size()).isEqualTo(1);
//        }
//
//        @DisplayName("특정 보기와 내용이 겹치는 모든 선지 선택 실패 - DTO Validation")
//        @Test
//        public void createDupContentChoicesFailWrongDto(){
//            ChoiceIdDto dto = new ChoiceIdDto();
//            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL + descriptionId, HttpMethod.DELETE, new HttpEntity<>(dto), new ParameterizedTypeReference<List<ErrorMsgDto>>() {
//            });
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
//            assertThat(response.getBody().size()).isEqualTo(1);
//        }
//
//        @DisplayName("특정 보기와 내용이 겹치는 모든 선지 선택 실패 - 존재하지 않은 보기 id입력")
//        @Test
//        public void createDupContentChoicesFailNotExistDescription() {
//            ChoiceIdDto dto = new ChoiceIdDto(choice3.getId());
//            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL + "123123", HttpMethod.DELETE,
//                    new HttpEntity<>(dto), new ParameterizedTypeReference<List<ErrorMsgDto>>() {});
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto("존재하지 않는 보기 ID입니다.")));
//        }
//
//        @DisplayName("특정 보기와 내용이 겹치는 모든 선지 선택 실패 - 존재하지 않은 선지 id입력")
//        @Test
//        public void createDupContentChoicesFailNotExistChoice() {
//            ChoiceIdDto dto = new ChoiceIdDto(123123L);
//            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL + descriptionId, HttpMethod.DELETE,
//                    new HttpEntity<>(dto), new ParameterizedTypeReference<List<ErrorMsgDto>>() {});
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto("존재하지 않는 선지 ID입니다.")));
//        }
//
//        @DisplayName("특정 보기와 내용이 겹치는 모든 선지 선택 실패 - 존재는 하지만 dupContent에 저장되지 않은 선지 삭제하려할때")
//        @Test
//        public void createDupContentChoicesFailNotSavedChoice() {
//            ChoiceIdDto dto = new ChoiceIdDto(choice5.getId());
//            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL + descriptionId, HttpMethod.DELETE,
//                    new HttpEntity<>(dto), new ParameterizedTypeReference<List<ErrorMsgDto>>() {});
//
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
//            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto("해당 보기와 내용이 겹친 선지가 아닙니다.")));
//        }
//    }
//
//
//}
