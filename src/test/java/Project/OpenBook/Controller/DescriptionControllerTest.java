//package Project.OpenBook.Controller;
//
//import Project.OpenBook.Domain.Category.Domain.Category;
//import Project.OpenBook.Domain.Chapter.Domain.Chapter;
//import Project.OpenBook.Domain.Description.Domain.Description;
//import Project.OpenBook.Domain.Description.Dto.DescriptionCreateDto;
//import Project.OpenBook.Domain.Description.Dto.DescriptionDto;
//import Project.OpenBook.Domain.Description.Dto.DescriptionUpdateDto;
//import Project.OpenBook.Handler.Exception.error.ErrorDto;
//import Project.OpenBook.Handler.Exception.error.ErrorMsgDto;
//import Project.OpenBook.Domain.Category.Repository.CategoryRepository;
//import Project.OpenBook.Domain.Chapter.Repo.ChapterRepository;
//import Project.OpenBook.Domain.StudyProgress.ChapterSection.Repository.ChapterSectionRepository;
//import Project.OpenBook.Domain.Description.Repository.DescriptionRepository;
//import Project.OpenBook.Domain.Topic.Repo.TopicRepository;
//import Project.OpenBook.Domain.StudyProgress.TopicProgress.Repository.TopicProgressRepository;
//import Project.OpenBook.Domain.Topic.Domain.Topic;
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
//class DescriptionControllerTest {
//
//    @LocalServerPort
//    protected int port;
//
//    @Autowired
//    TestRestTemplate restTemplate;
//
//    @Autowired
//    ChapterRepository chapterRepository;
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
//    private final String prefix = "http://localhost:";
//
//    private String URL,suffix;
//
//    Topic topic1, topic2;
//
//    Description desc1, desc2, desc3, desc4, desc5;
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
//         * desc1,2,3 -> topic1
//         * desc4,5 -> topic2
//         */
////        desc1 = new Description("desc1", topic1);
////        desc2 = new Description("desc2", topic1);
////        desc3 = new Description("desc3", topic1);
////
////        desc4 = new Description("desc4", topic2);
////        desc5 = new Description("desc5", topic2);
//
//        descriptionRepository.saveAllAndFlush(Arrays.asList(desc1, desc2, desc3, desc4, desc5));
//    }
//    private void baseClear() {
//        descriptionRepository.deleteAllInBatch();
//        topicRepository.deleteAllInBatch();
//        chapterRepository.deleteAllInBatch();
//        categoryRepository.deleteAllInBatch();
//    }
//
//
//
//    @Nested
//    @DisplayName("특정 보기 조회 - GET /admin/descriptions/{choiceId}")
//    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
//    public class queryDescription{
//        @BeforeAll
//        public void init(){
//            suffix = "/admin/descriptions/";
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
//        public void queryDescriptionsSuccess() {
//            Long descId = desc1.getId();
//
//            ResponseEntity<DescriptionDto> response = restTemplate.getForEntity(URL + descId, DescriptionDto.class);
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(new DescriptionDto(desc1));
//        }
//
//        @DisplayName("특정 보기 조회 실패 - 존재하지 않는 보기 id 입력")
//        @Test
//        public void queryDescriptionFail(){
//            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL + "-1", HttpMethod.GET,
//                    null, new ParameterizedTypeReference<List<ErrorMsgDto>>() {});
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto("존재하지 않는 보기 ID입니다.")));
//        }
//    }
//
//    @Nested
//    @DisplayName("보기 추가 - POST /admin/descriptions/")
//    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
//    public class createDescription{
//        @BeforeAll
//        public void init(){
//            suffix = "/admin/descriptions/";
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
//        @DisplayName("보기 추가 성공")
//        @Test
//        public void createDescriptionSuccess() {
//            String[] contentArr = {"content"};
//            DescriptionCreateDto dto = new DescriptionCreateDto("title1", contentArr);
//            ResponseEntity<Void> response = restTemplate.postForEntity(URL, dto, Void.class);
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
//
//            List<Description> descriptionList = descriptionRepository.findDescriptionsByTopic("title1");
//            assertThat(descriptionList.size()).isEqualTo(4); // 기존 3개 + 추가한 1개
//        }
//
//        @DisplayName("보기 추가 실패 - DTO Validation")
//        @Test
//        public void createDescriptionFailWrongDto(){
//            DescriptionCreateDto dto = new DescriptionCreateDto();
//            ResponseEntity<List<ErrorDto>> response = restTemplate.exchange(URL, HttpMethod.POST, new HttpEntity<>(dto), new ParameterizedTypeReference<List<ErrorDto>>() {
//            });
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
//            assertThat(response.getBody().size()).isEqualTo(2);
//        }
//
//        @DisplayName("보기 추가 실패 - 존재하지 않는 토픽 제목 입력")
//        @Test
//        public void createDescriptionFailNotExistTopic(){
//            String[] contentArr = {"content"};
//            DescriptionCreateDto dto = new DescriptionCreateDto("title-1", contentArr);
//
//            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL,HttpMethod.POST,
//                    new HttpEntity<>(dto), new ParameterizedTypeReference<List<ErrorMsgDto>>(){});
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto("존재하지 않는 토픽 제목입니다.")));
//        }
//
//        @DisplayName("보기 추가 실패 - 중복된 내용의 보기 입력")
//        @Test
//        public void createDescriptionFailDupContent(){
//            String[] contentArr = {"desc1"};
//            DescriptionCreateDto dto = new DescriptionCreateDto("title1", contentArr);
//
//            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL,HttpMethod.POST,
//                    new HttpEntity<>(dto), new ParameterizedTypeReference<List<ErrorMsgDto>>(){});
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
//            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto("중복된 보기내용입니다.")));
//        }
//    }
//
//    @Nested
//    @DisplayName("보기 수정 - PATCH /admin/descriptions/{descriptionId}")
//    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
//    public class updateDescription{
//
//        private Long descriptionId;
//        @BeforeAll
//        public void init(){
//            suffix = "/admin/descriptions/";
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
//        @DisplayName("보기 수정 성공")
//        @Test
//        public void updateDescriptionSuccess() {
//            DescriptionUpdateDto dto = new DescriptionUpdateDto("after content");
//
//            ResponseEntity<Void> response = restTemplate.exchange(URL + descriptionId, HttpMethod.PATCH, new HttpEntity<>(dto), Void.class);
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//
//            List<Description> descriptionList = descriptionRepository.findDescriptionsByTopic("title1");
//            assertThat(descriptionList.size()).isEqualTo(3); // 기존의 3개
//
//            Description description = descriptionRepository.findById(descriptionId).get();
//            assertThat(description.getContent()).isEqualTo("after content");
//        }
//
//        @DisplayName("보기 수정 실패 - DTO Validation")
//        @Test
//        public void updateDescriptionWrongDto(){
//            DescriptionUpdateDto dto = new DescriptionUpdateDto();
//            ResponseEntity<List<ErrorDto>> response = restTemplate.exchange(URL + descriptionId, HttpMethod.PATCH, new HttpEntity<>(dto), new ParameterizedTypeReference<List<ErrorDto>>() {
//            });
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
//            assertThat(response.getBody().size()).isEqualTo(1);
//        }
//
//        @DisplayName("보기 수정 실패 - 존재하지 않는 보기id 입력")
//        @Test
//        public void updateDescriptionNotExistDesc(){
//            DescriptionUpdateDto dto = new DescriptionUpdateDto("content1");
//
//            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL + "123123", HttpMethod.PATCH,
//                    new HttpEntity<>(dto), new ParameterizedTypeReference<List<ErrorMsgDto>>() {});
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//            assertThat(response.getBody().size()).isEqualTo(1);
//            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto("존재하지 않는 보기 ID입니다.")));
//        }
//
//        @DisplayName("보기 수정 실패 - 중복되는 보기 내용 입력")
//        @Test
//        public void updateDescriptionFailDupContent(){
//            DescriptionUpdateDto dto = new DescriptionUpdateDto("desc3");
//
//            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL + descriptionId, HttpMethod.PATCH,
//                    new HttpEntity<>(dto), new ParameterizedTypeReference<List<ErrorMsgDto>>() {});
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
//            assertThat(response.getBody().size()).isEqualTo(1);
//            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto("중복된 보기내용입니다.")));
//        }
//    }
//
//    @Nested
//    @DisplayName("보기 삭제 - DELETE /admin/descriptions/{descriptionId}")
//    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
//    public class deleteDescription{
//
//        private Long descriptionId;
//        @BeforeAll
//        public void init(){
//            suffix = "/admin/descriptions/";
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
//        @DisplayName("보기 삭제 성공")
//        @Test
//        public void deleteDescriptionSuccess() {
//            ResponseEntity<Void> response = restTemplate.exchange(URL + descriptionId, HttpMethod.DELETE, null, Void.class);
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//
//            List<Description> descriptionList = descriptionRepository.findDescriptionsByTopic("title1");
//            assertThat(descriptionList.size()).isEqualTo(2); // 기존의 3개에서 1개 제거
//            assertThat(descriptionRepository.findById(descriptionId).isEmpty()).isTrue();
//        }
//
//
//        @DisplayName("보기 삭제 실패 - 존재하지 않는 보기id 입력")
//        @Test
//        public void deleteDescriptionFailNotExistChoice(){
//            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL + "123123", HttpMethod.DELETE,
//                    null, new ParameterizedTypeReference<List<ErrorMsgDto>>() {});
//
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto("존재하지 않는 보기 ID입니다.")));
//        }
//    }
//
//}
