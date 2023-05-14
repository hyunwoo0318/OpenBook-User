package Project.OpenBook.Controller;

import Project.OpenBook.Domain.Category;
import Project.OpenBook.Domain.Chapter;
import Project.OpenBook.Domain.Choice;
import Project.OpenBook.Domain.Topic;
import Project.OpenBook.Dto.choice.ChoiceAddDto;
import Project.OpenBook.Dto.choice.ChoiceContentIdDto;
import Project.OpenBook.Dto.choice.ChoiceDto;
import Project.OpenBook.Dto.choice.ChoiceUpdateDto;
import Project.OpenBook.Dto.error.ErrorDto;
import Project.OpenBook.Repository.CategoryRepository;
import Project.OpenBook.Repository.ChapterRepository;
import Project.OpenBook.Repository.topic.TopicRepository;
import Project.OpenBook.Repository.choice.ChoiceRepository;
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
import org.springframework.test.context.TestPropertySource;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = { "spring.config.location=classpath:application-test.yml" })
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ChoiceControllerTest {

    @LocalServerPort
    protected int port;

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    ChapterRepository chapterRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    TopicRepository topicRepository;

    @Autowired
    ChoiceRepository choiceRepository;

    private final String prefix = "http://localhost:";

    String URL;

    Topic topic1, topic2;

    Choice choice1, choice2, choice3, choice4, choice5;


    private final String[]  categoryNameList ={"사건","유물", "인물"};
    private Chapter chapter;
    private List<Category> categoryList;

    @BeforeAll
    public void initTestForChoiceController() {
        choiceRepository.deleteAllInBatch();
        topicRepository.deleteAllInBatch();
        chapterRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
        URL = prefix + port;
        restTemplate = restTemplate.withBasicAuth("admin1", "admin1");
        restTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        initEntity();
    }


    private void initEntity(){
        Chapter chapter = new Chapter("1단원", 1);
        Category category = new Category("사건");
        chapterRepository.save(chapter);
        categoryRepository.save(category);

        topic1 = new Topic("topic1", null, null, 0, 0, "detail1", chapter, category);
        topic2 = new Topic("topic2", null, null, 0, 0, "detail2", chapter, category);

        topicRepository.save(topic1);
        topicRepository.save(topic2);
    }

    /**
     * choice1,2,3 -> topic1
     * choice4,5 -> topic2
     */
    @BeforeEach
    public void resetChoice(){
        choiceRepository.deleteAllInBatch();

        choice1 = new Choice("choice1", topic1);
        choice2 = new Choice("choice2", topic1);
        choice3 = new Choice("choice3", topic1);

        choice4 = new Choice("choice4", topic2);
        choice5 = new Choice("choice5", topic2);

        choiceRepository.save(choice1);
        choiceRepository.save(choice2);
        choiceRepository.save(choice3);
        choiceRepository.save(choice4);
        choiceRepository.save(choice5);
    }


    @DisplayName("특정 topic의 모든 선지를 보여주기 성공 - GET /admin/topics/{topicTitle}/choices/")
    @Test
    public void getChoicesInTopicsSuccess() {
        String topicTitle = "topic1";
        ResponseEntity<List<ChoiceDto>> response = restTemplate.exchange(URL + "/admin/topics/{topicTitle}/choices/", HttpMethod.GET, null, new ParameterizedTypeReference<List<ChoiceDto>>() {}, topicTitle);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<ChoiceDto> body = response.getBody();
        Set<String> contentSet = body.stream().map(c -> c.getContent()).collect(Collectors.toSet());

        assertThat(contentSet.size()).isEqualTo(3);
        assertThat(contentSet.contains("choice1")).isTrue();
        assertThat(contentSet.contains("choice2")).isTrue();
        assertThat(contentSet.contains("choice3")).isTrue();
        assertThat(contentSet.contains("choice4")).isFalse();
        assertThat(contentSet.contains("choice5")).isFalse();
    }

    @DisplayName("특정 선지 보여주기 성공 - GET /admin/choices/{choiceId}")
    @Test
    public void getChoiceSuccess(){
        String choiceId = choice1.getId().toString();
        ResponseEntity<ChoiceDto> response = restTemplate.getForEntity(URL + "/admin/choices/" + choiceId, ChoiceDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getContent()).isEqualTo("choice1");
        assertThat(response.getBody().getTopic()).isEqualTo("topic1");
    }

    @DisplayName("존재하지 않는 선지 조회 요청시 - GET /admin/choices/{choiceId}")
    @Test
    public void getChoiceFail() {
        ResponseEntity<ChoiceDto> response = restTemplate.getForEntity(URL + "/admin/choices/3333333" , ChoiceDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @DisplayName("3개의 선지 추가 성공 - POST /admin/choices")
    @Test
    public void add3ChoiceSuccess() {
        String title = topic1.getTitle();
        String[] contentList = {"ac1", "ac2", "ac3"};

        ChoiceAddDto dto = new ChoiceAddDto(title, contentList);

        ResponseEntity<Void> response = restTemplate.postForEntity(URL + "/admin/choices/", dto, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        List<Choice> choiceList = choiceRepository.queryChoiceByTopicTitle(title);
        assertThat(choiceList.size()).isEqualTo(6); // 기존 3개 + 추가한 3개
        Set<String> choiceSet = choiceList.stream().map(c -> c.getContent()).collect(Collectors.toSet());

        assertThat(choiceSet.contains("ac1")).isTrue();
        assertThat(choiceSet.contains("ac2")).isTrue();
        assertThat(choiceSet.contains("ac3")).isTrue();
    }

    @DisplayName("잘못된 입력으로 인한 선지 추가 실패 - POST /admin/choices")
    @Test
    public void addChoicesFail(){
        String title = topic1.getTitle();
        String[] contentList = {"ac1", "ac2", "ac3"};

        ChoiceAddDto wrongDto1 = new ChoiceAddDto("", contentList);
        ChoiceAddDto wrongDto2 = new ChoiceAddDto("title",null);
        ChoiceAddDto wrongDto3 = new ChoiceAddDto("wrongTopicTitle",contentList);

        ResponseEntity<List<ErrorDto>> response1 = restTemplate.exchange(URL + "/admin/choices/", HttpMethod.POST, new HttpEntity<>(wrongDto1), new ParameterizedTypeReference<List<ErrorDto>>() {});
        ResponseEntity<List<ErrorDto>> response2 =restTemplate.exchange(URL + "/admin/choices/", HttpMethod.POST, new HttpEntity<>(wrongDto2), new ParameterizedTypeReference<List<ErrorDto>>() {});
        ResponseEntity<List<ErrorDto>> response3 =restTemplate.exchange(URL + "/admin/choices/", HttpMethod.POST, new HttpEntity<>(wrongDto3), new ParameterizedTypeReference<List<ErrorDto>>() {});

        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response3.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        assertThat(response1.getBody().get(0)).isEqualTo(new ErrorDto("topicTitle", "토픽제목을 입력해주세요."));
        assertThat(response2.getBody().get(0)).isEqualTo(new ErrorDto("choiceArr", "하나이상의 선지를 입력해주세요."));
        assertThat(response3.getBody().get(0)).isEqualTo(new ErrorDto("topicTitle", "정확한 토픽제목을 입력해주세요"));

        List<Choice> choiceList = choiceRepository.queryChoiceByTopicTitle(title);
        assertThat(choiceList.size()).isEqualTo(3); // 기존의 3개 (추가되지 않음)
    }

    @DisplayName("선지 수정 성공 - PATCH admin/choices/{choiceId}")
    @Test
    public void updateChoiceSuccess() {
        Long id = choice1.getId();
        String content = "content1 with update";
        ChoiceUpdateDto choiceUpdateDto = new ChoiceUpdateDto(content);

        ResponseEntity<Void> response = restTemplate.exchange(URL + "/admin/choices/" + id, HttpMethod.PATCH, new HttpEntity<>(choiceUpdateDto), Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Optional<Choice> choiceOptional = choiceRepository.findById(id);
        assertThat(choiceOptional.isEmpty()).isFalse();
        assertThat(choiceOptional.get().getContent()).isEqualTo(content);
    }
    
    @DisplayName("선지 수정 실패 - PATCH admin/choices/{choiceId}")
    @Test
    public void updateChoiceFail(){
        Long id = choice1.getId();

        //request body는 유효하지만 존재하지 않는 choiceId를 넣는 경우
        String content = "content1 with update";
        ChoiceUpdateDto choiceUpdateDto = new ChoiceUpdateDto(content);

        //보기 내용을 넣지 않는 경우
        ChoiceUpdateDto wrongDto = new ChoiceUpdateDto("");
        ResponseEntity<List<ErrorDto>> response1 = restTemplate.exchange(URL + "/admin/choices/" + id, HttpMethod.PATCH, new HttpEntity<>(wrongDto), new ParameterizedTypeReference<List<ErrorDto>>() {});
        ResponseEntity<Void> response2 = restTemplate.exchange(URL + "/admin/choices/2222323", HttpMethod.PATCH, new HttpEntity<>(choiceUpdateDto), Void.class);

        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response1.getBody().get(0)).isEqualTo(new ErrorDto("content", "선지 내용을 입력해주세요."));
        Optional<Choice> choiceOptional = choiceRepository.findById(id);
        assertThat(choiceOptional.isEmpty()).isFalse();
        assertThat(choiceOptional.get().getContent()).isNotEqualTo(content);

        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @DisplayName("선지 삭제 성공 - DELETE admin/choices/{choiceId}")
    @Test
    public void deleteChoiceSuccess(){
        Choice choice = new Choice("temp1", topic1);
        choiceRepository.save(choice);
        Long id = choice.getId();

        ResponseEntity<Void> response = restTemplate.exchange(URL + "/admin/choices/" + id, HttpMethod.DELETE, null, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Optional<Choice> choiceOptional = choiceRepository.findById(id);

        assertThat(choiceOptional.isEmpty()).isTrue();
    }

    @DisplayName("선지 삭제 실패 - DELETE admin/choices/{choiceId}")
    @Test
    public void deleteChoiceFail(){
        ResponseEntity<Void> response = restTemplate.exchange(URL + "/admin/choices/" + 22222, HttpMethod.DELETE, null, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    }



//    @DisplayName("여러개의 선지 변경 성공 - PATCH /admin/choices")
//    @Test
//    public void updateChoicesSuccess(){
//        ChoiceContentIdDto dto1 = new ChoiceContentIdDto("afterChoice1", choice1.getId());
//        ChoiceContentIdDto dto2 = new ChoiceContentIdDto("afterChoice2", choice2.getId());
//        ChoiceContentIdDto dto3 = new ChoiceContentIdDto("afterChoice3", choice3.getId());
//
//        List<ChoiceContentIdDto> dtoList = Arrays.asList(dto1, dto2, dto3);
//        ChoiceUpdateDto choiceUpdateDto = new ChoiceUpdateDto(dtoList);
//
//        ResponseEntity<Void> response = restTemplate.exchange(URL + "/admin/choices/", HttpMethod.PATCH, new HttpEntity<>(choiceUpdateDto), Void.class);
//
//        List<Choice> choiceList = choiceRepository.queryChoiceByTopicTitle(topic1.getTitle());
//        Set<String> contentSet = choiceList.stream().map(c -> c.getContent()).collect(Collectors.toSet());
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//
//        assertThat(contentSet.contains("choice1")).isFalse();
//        assertThat(contentSet.contains("choice2")).isFalse();
//        assertThat(contentSet.contains("choice3")).isFalse();
//
//        assertThat(contentSet.contains("afterChoice1")).isTrue();
//        assertThat(contentSet.contains("afterChoice2")).isTrue();
//        assertThat(contentSet.contains("afterChoice3")).isTrue();
//    }
//
//    @DisplayName("여러개의 선지 변경 실패 - PATCH /admin/choices")
//    @Test
//    public void updateChoicesFail(){
//        ChoiceContentIdDto dto = new ChoiceContentIdDto("afterChoice1", 333333L);
//        List<ChoiceContentIdDto> wrongDtoList = Arrays.asList(dto);
//
//        ChoiceUpdateDto wrongDto1 = new ChoiceUpdateDto(new ArrayList<>());
//        ChoiceUpdateDto wrongDto2 = new ChoiceUpdateDto(wrongDtoList);
//
//        ResponseEntity<List<ErrorDto>> response1 = restTemplate.exchange(URL + "/admin/choices/", HttpMethod.PATCH, new HttpEntity<>(wrongDto1), new ParameterizedTypeReference<List<ErrorDto>>() {});
//        ResponseEntity<Void> response2 = restTemplate.exchange(URL + "/admin/choices/", HttpMethod.PATCH, new HttpEntity<>(wrongDto2), Void.class);
//
//        List<Choice> choiceList = choiceRepository.queryChoiceByTopicTitle(topic1.getTitle());
//        Set<String> contentSet = choiceList.stream().map(c -> c.getContent()).collect(Collectors.toSet());
//
//        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
//        assertThat(response1.getBody().get(0)).isEqualTo(new ErrorDto("choiceList", "하나 이상의 선지를 입력해주세요."));
//
//        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//
//        assertThat(contentSet.contains("afterChoice1")).isFalse();
//    }
//
//    @DisplayName("여러개의 선지 삭제 - DELETE /admin/choices")
//    @Test
//    public void deleteChoicesSuccess(){
//        List<Long> idList = Arrays.asList(choice4.getId(), choice5.getId());
//
//        ResponseEntity<Void> response = restTemplate.exchange(URL + "/admin/choices/", HttpMethod.DELETE, new HttpEntity<>(idList), Void.class);
//
//        List<Choice> choiceList = choiceRepository.queryChoiceByTopicTitle(topic2.getTitle());
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(choiceList.size()).isEqualTo(0);
//    }
//
//    @DisplayName("존재하지 않는 선지 삭제 요청 - DELETE /admin/choices")
//    @Test
//    public void deleteChoicesFail(){
//        List<Long> idList = Arrays.asList(333333L, 44444444L);
//
//        ResponseEntity<Void> response = restTemplate.exchange(URL + "/admin/choices/", HttpMethod.DELETE, new HttpEntity<>(idList), Void.class);
//
//        List<Choice> choiceList = choiceRepository.queryChoiceByTopicTitle(topic2.getTitle());
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//        assertThat(choiceList.size()).isEqualTo(2);
//    }
}