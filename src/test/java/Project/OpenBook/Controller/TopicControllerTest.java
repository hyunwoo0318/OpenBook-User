package Project.OpenBook.Controller;

import Project.OpenBook.Domain.Category;
import Project.OpenBook.Domain.Chapter;
import Project.OpenBook.Domain.Topic;
import Project.OpenBook.Dto.error.ErrorDto;
import Project.OpenBook.Dto.topic.TopicDto;
import Project.OpenBook.Repository.CategoryRepository;
import Project.OpenBook.Repository.ChapterRepository;
import Project.OpenBook.Repository.topic.TopicRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = { "spring.config.location=classpath:application-test.yml" })
class TopicControllerTest {
    @LocalServerPort
    int port;
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private ChapterRepository chapterRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    TestRestTemplate restTemplate;

    private final String prefix = "http://localhost:";
    private final String surfix = "/admin/topics";
    private String URL;
    int chapterNum = 1;
    String categoryName = "유물";

    @BeforeEach
    public void deleteAllAndInit() {
        topicRepository.deleteAllInBatch();
        chapterRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
        URL = prefix + port + surfix;
        restTemplate = restTemplate.withBasicAuth("admin1", "admin1");
        restTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        Chapter chapter = new Chapter("ct1", chapterNum);
        Category category = new Category(categoryName);

        chapterRepository.saveAndFlush(chapter);
        categoryRepository.saveAndFlush(category);

        Topic topic = Topic.builder()
                .title("title1")
                .detail("detail1")
                .chapter(chapter)
                .category(category)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .build();

        topicRepository.saveAndFlush(topic);
    }


    @DisplayName("새로운 상세정보 추가 - POST /admin/topics")
    @Test
    public void createTopicSuccess() {
        TopicDto topicDto = new TopicDto(chapterNum, "title123", categoryName, null, null, "detail1");

        ResponseEntity<Void> response = restTemplate.postForEntity(URL, topicDto, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Chapter chapter = chapterRepository.findOneByNumber(chapterNum).get();
        assertThat(topicRepository.findTopicByTitle("title123").isPresent()).isTrue();
    }

    @DisplayName("새로운 상세정보 추가 실패(DTO validation) - POST /admin/topics")
    @Test
    public void creaeteTopicFailWrongDTO(){

        //필수 입력 조건인 chapterNum,title,categoryName 생략
        TopicDto topicDto = new TopicDto();

        ResponseEntity<List<ErrorDto>> response = restTemplate.exchange(URL, HttpMethod.POST, new HttpEntity<>(topicDto), new ParameterizedTypeReference<List<ErrorDto>>() {
        });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().size()).isEqualTo(3);
    }

    @DisplayName("새로운 상세정보 추가 실패 (중복된 입력, 존재하지않는 카테고리 & 단원) - POST admin/chapters")
    @Test
    public void createTopicFailWrongInput() {
        //제목이 중복되는 경우
        TopicDto wrongDto1 = new TopicDto(chapterNum, "title1", categoryName, null, null, "detail123");

        //존재하지 않는 단원번호를 입력한경우
        TopicDto wrongDto2 = new TopicDto(2, "title123", categoryName, null, null, "detail123");

        //존재하지 않는 카테고리를 입력한경우
        TopicDto wrongDto3 = new TopicDto(chapterNum, "title123", "사건", null, null, "detail123");

        ResponseEntity<List<ErrorDto>> response1 = restTemplate.exchange(URL, HttpMethod.POST, new HttpEntity<>(wrongDto1), new ParameterizedTypeReference<List<ErrorDto>>() {
        });
        ResponseEntity<List<ErrorDto>> response2 = restTemplate.exchange(URL, HttpMethod.POST, new HttpEntity<>(wrongDto2), new ParameterizedTypeReference<List<ErrorDto>>() {
        });
        ResponseEntity<List<ErrorDto>> response3 = restTemplate.exchange(URL, HttpMethod.POST, new HttpEntity<>(wrongDto3), new ParameterizedTypeReference<List<ErrorDto>>() {
        });

        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response1.getBody().get(0)).isEqualTo(new ErrorDto("title", "이미 존재하는 제목입니다."));

        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response2.getBody().get(0)).isEqualTo(new ErrorDto("chapter", "존재하지않는 단원입니다."));

        assertThat(response3.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response3.getBody().get(0)).isEqualTo(new ErrorDto("category", "존재하지않는 카테고리입니다."));
    }

    @DisplayName("기존 상세정보 변경 성공 - PATCH admin/topics")
    @Test
    public void updateTopicSuccess() {
        TopicDto dto = new TopicDto(chapterNum, "title2", categoryName, null, null, "detail123");

        ResponseEntity<Void> response = restTemplate.exchange(URL + "/title1", HttpMethod.PATCH, new HttpEntity<>(dto), Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(topicRepository.findTopicByTitle("title1").isEmpty()).isTrue();
        assertThat(topicRepository.findTopicByTitle("title2").isPresent()).isTrue();
        assertThat(topicRepository.findTopicByTitle("title2").get().getDetail()).isEqualTo("detail123");
    }

    @DisplayName("기존 상세정보 변경 실패 - PATCH admin/topics")
    @Test
    public void updateTopicFail() {
        //TODO : 구현
    }

    @DisplayName("기존 상세정보 삭제 성공 - DELETE admin/topics")
    @Test
    public void deleteTopicSuccess() {
        ResponseEntity<Void> response = restTemplate.exchange(URL + "/title1", HttpMethod.DELETE, null, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(topicRepository.findTopicByTitle("title1").isEmpty()).isTrue();
    }

    @DisplayName("기존 상세정보 삭제 실패 - DELETE admin/topics")
    @Test
    public void deleteTopicFail() {
        ResponseEntity<Void> response = restTemplate.exchange(URL + "/title2", HttpMethod.DELETE, null, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(topicRepository.findTopicByTitle("title1").isPresent()).isTrue();
    }
}