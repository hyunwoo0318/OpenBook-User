package Project.OpenBook.Controller;

import Project.OpenBook.Domain.Category;
import Project.OpenBook.Domain.Chapter;
import Project.OpenBook.Domain.Topic;
import Project.OpenBook.Dto.CategoryDto;
import Project.OpenBook.Dto.ErrorDto;
import Project.OpenBook.Repository.CategoryRepository;
import Project.OpenBook.Repository.ChapterRepository;
import Project.OpenBook.Repository.TopicRepository;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = { "spring.config.location=classpath:application-test.yml" })
class CategoryControllerTest {

    @LocalServerPort
    int port;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    TopicRepository topicRepository;

    @Autowired
    ChapterRepository chapterRepository;

    @Autowired
    TestRestTemplate restTemplate;


    private final String prefix = "http://localhost:";
    private final String surfix = "/admin/categories";

    private final String categoryName = "유물";

    String URL;


    @BeforeEach
    public void deleteAll() {
        topicRepository.deleteAllInBatch();
        chapterRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
        URL = prefix + port + surfix;
        restTemplate = restTemplate.withBasicAuth("admin1", "admin1");
        restTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    }

    @DisplayName("카테고리 전체 조회 성공 - GET /admin/categories")
    @Test
    public void queryCategoriesSuccess() {
        List<Category> categoryList = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            categoryList.add(new Category("title" + i));
        }
        categoryRepository.saveAllAndFlush(categoryList);

        ResponseEntity<List<String>> response = restTemplate.exchange(URL, HttpMethod.GET, null, new ParameterizedTypeReference<List<String>>() {
        });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(Arrays.asList("title1", "title2", "title3", "title4", "title5"));
    }

    @DisplayName("새로운 카테고리 저장 성공 - POST /admin/chapters")
    @Test
    public void createNewCategorySuccess() {
        CategoryDto categoryDto = new CategoryDto(categoryName);

        ResponseEntity<Void> response = restTemplate.postForEntity(URL, categoryDto, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(categoryRepository.findCategoryByName(categoryName).isPresent()).isTrue();
    }

    @DisplayName("새로운 카테고리 저장 실패 - POST /admin/chapters")
    @Test
    public void createNewCategoryFail() {

        categoryRepository.saveAndFlush(new Category(categoryName));

        //이름 입력x
        CategoryDto wrongDto1 = new CategoryDto();
        //이미 존재하는 카테고리
        CategoryDto wrongDto2 = new CategoryDto(categoryName);

        ResponseEntity<List<ErrorDto>> response1 = restTemplate.exchange(URL, HttpMethod.POST, new HttpEntity<>(wrongDto1), new ParameterizedTypeReference<List<ErrorDto>>() {
        });
        ResponseEntity<List<ErrorDto>> response2 = restTemplate.exchange(URL, HttpMethod.POST, new HttpEntity<>(wrongDto2), new ParameterizedTypeReference<List<ErrorDto>>() {
        });

        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response1.getBody().get(0)).isEqualTo(new ErrorDto("name", "카테고리 이름을 입력해주세요"));

        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response2.getBody().get(0)).isEqualTo(new ErrorDto("name", "이미 존재하는 카테고리입니다."));
    }

    @DisplayName("카테고리 수정 성공 - PATCH admin/categories")
    @Test
    public void updateCategorySuccess() {
        categoryRepository.saveAndFlush(new Category(categoryName));
        String afterName = "전쟁";
        CategoryDto categoryDto = new CategoryDto(afterName);

        ResponseEntity<Void> response = restTemplate.exchange(URL + "/" + categoryName, HttpMethod.PATCH, new HttpEntity<>(categoryDto), Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(categoryRepository.findCategoryByName(categoryName).isEmpty()).isTrue();
        assertThat(categoryRepository.findCategoryByName(afterName).isPresent()).isTrue();
    }

    @DisplayName("카테고리 수정 실패 - PATCH admin/categories")
    @Test
    public void updateCategoryFail() {
        List<Category> categoryList = Arrays.asList(new Category("전쟁"), new Category("인물"));
        categoryRepository.saveAllAndFlush(categoryList);

        CategoryDto dto = new CategoryDto("사건");
        //이름 입력x
        CategoryDto wrongDto1 = new CategoryDto();
        //이미 존재하는 이름인 경우
        CategoryDto wrongDto2 = new CategoryDto("전쟁");

        ResponseEntity<List<ErrorDto>> response1 = restTemplate.exchange(URL + "/인물", HttpMethod.PATCH, new HttpEntity<>(wrongDto1), new ParameterizedTypeReference<List<ErrorDto>>() {
        });
        ResponseEntity<List<ErrorDto>> response2 = restTemplate.exchange(URL + "/인물", HttpMethod.PATCH, new HttpEntity<>(wrongDto2), new ParameterizedTypeReference<List<ErrorDto>>() {
        });
        //존재하지 않는 카테고리를 변경하려 시도하는 경우
        ResponseEntity<Void> response3 = restTemplate.exchange(URL + "/유물", HttpMethod.PATCH, new HttpEntity<>(dto),Void.class);

        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response1.getBody().get(0)).isEqualTo(new ErrorDto("name", "카테고리 이름을 입력해주세요"));

        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response2.getBody().get(0)).isEqualTo(new ErrorDto("name", "이미 존재하는 카테고리입니다."));

        assertThat(response3.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(categoryRepository.findCategoryByName("사건").isEmpty()).isTrue();
    }

    @DisplayName("카테고리 삭제 성공 - DELETE admin/categories")
    @Test
    public void deleteCategorySuccess() {
        Category category = new Category(categoryName);
        Chapter chapter = new Chapter("ct1", 1);
        chapterRepository.saveAndFlush(chapter);
        categoryRepository.saveAndFlush(category);
        Topic topic = new Topic("title1", null, null, 0, 0, "detail1", chapter, category);
        topicRepository.saveAndFlush(topic);

        ResponseEntity<Void> response = restTemplate.exchange(URL + "/" + categoryName, HttpMethod.DELETE, null, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(topicRepository.findTopicByTitle("title1").get().getCategory()).isNull();
        assertThat(categoryRepository.findCategoryByName(categoryName).isEmpty()).isTrue();
    }

    @DisplayName("카테고리 삭제 실패 - DELETE admin/categories")
    @Test
    public void deleteCategoryFail() {
        Category category = new Category(categoryName);
        categoryRepository.saveAndFlush(category);


        //존재하지 않는 카테고리 삭제 시도
        ResponseEntity<Void> response = restTemplate.exchange(URL + "/사건" + categoryName, HttpMethod.DELETE, null, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }


}