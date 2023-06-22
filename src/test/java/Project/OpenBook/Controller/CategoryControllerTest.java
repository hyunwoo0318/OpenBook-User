package Project.OpenBook.Controller;

import Project.OpenBook.Domain.Category;
import Project.OpenBook.Domain.Chapter;
import Project.OpenBook.Domain.Topic;
import Project.OpenBook.Dto.category.CategoryDto;
import Project.OpenBook.Dto.error.ErrorDto;
import Project.OpenBook.Dto.error.ErrorMsgDto;
import Project.OpenBook.Repository.category.CategoryRepository;
import Project.OpenBook.Repository.chapter.ChapterRepository;
import Project.OpenBook.Repository.choice.ChoiceRepository;
import Project.OpenBook.Repository.topic.TopicRepository;
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

import javax.transaction.Transactional;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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
    ChoiceRepository choiceRepository;

    @Autowired
    TestRestTemplate restTemplate;


    private final String prefix = "http://localhost:";
    private String suffix;

    private final String categoryName = "유물";
    private Category c1;

    String URL;

    private void initConfig() {
        URL = prefix + port + suffix;
        restTemplate = restTemplate.withBasicAuth("admin1", "admin1");
        restTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    }

    private void baseSetting() {
        c1 = new Category(categoryName);
        categoryRepository.saveAndFlush(c1);
    }

    @Nested
    @DisplayName("카테고리 전체 조회 - GET /admin/categories")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    public class queryCategories {
        @BeforeAll
        public void init(){
            suffix = "/admin/categories";
            initConfig();
        }

        @AfterEach
        public void clear() {
            categoryRepository.deleteAllInBatch();
        }

        @DisplayName("카테고리 전체 조회 성공")
        @Test
        public void queryCategoriesSuccess() {
            List<Category> categoryList = new ArrayList<>();
            for (int i = 1; i <= 5; i++) {
                categoryList.add(new Category("title" + i));
            }
            categoryRepository.saveAll(categoryList);
            ResponseEntity<List<String>> response = restTemplate.exchange(URL, HttpMethod.GET, null, new ParameterizedTypeReference<List<String>>() {
            });

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(Arrays.asList("title1", "title2", "title3", "title4", "title5"));
        }
    }

    @Nested
    @DisplayName("카테고리 저장 - POST /admin/categories")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    public class createCategory{

        @BeforeAll
        public void init(){
            categoryRepository.deleteAllInBatch();
            suffix = "/admin/categories";
            initConfig();
        }

        @AfterEach
        public void clear() {
            categoryRepository.deleteAllInBatch();
        }

        @BeforeEach
        public void setting() {
            baseSetting();
        }


        @DisplayName("새로운 카테고리 저장 성공")
        @Test
        public void createNewCategorySuccess(){
            CategoryDto categoryDto = new CategoryDto("인물");

            ResponseEntity<Void> response = restTemplate.postForEntity(URL, categoryDto, Void.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(categoryRepository.findCategoryByName(categoryName).isPresent()).isTrue();
        }

        @DisplayName("새로운 카테고리 저장 실패")
        @Test
        public void createNewCategoryFail() {
            //이름 입력x
            CategoryDto wrongDto1 = new CategoryDto();
            //이미 존재하는 카테고리
            CategoryDto wrongDto2 = new CategoryDto(categoryName);

            ResponseEntity<List<ErrorDto>> response1 = restTemplate.exchange(URL, HttpMethod.POST, new HttpEntity<>(wrongDto1), new ParameterizedTypeReference<List<ErrorDto>>() {});
            ResponseEntity<ErrorMsgDto> response2 = restTemplate.postForEntity(URL, wrongDto2, ErrorMsgDto.class);
            List<ErrorDto> body1 = response1.getBody();
            ErrorMsgDto body2 = response2.getBody();

            assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(body1.size()).isEqualTo(1);
            assertThat(body1.get(0)).usingRecursiveComparison().isEqualTo(new ErrorDto("name", "카테고리 이름을 입력해주세요"));

            assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(body2).usingRecursiveComparison().isEqualTo(new ErrorMsgDto("중복된 카테고리 이름입니다."));
        }
    }

    @Nested
    @DisplayName("카테고리 수정 - PATCH admin/categories")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    public class updateCategory{

        @BeforeAll
        public void init(){
            suffix = "/admin/categories";
            initConfig();
        }

        @AfterEach
        public void clear() {
            categoryRepository.deleteAllInBatch();
        }

        @BeforeEach
        public void setting(){
            baseSetting();
        }

        @DisplayName("카테고리 수정 성공")
        @Test
        public void updateCategorySuccess() {
            String afterName = "전쟁";
            CategoryDto categoryDto = new CategoryDto(afterName);

            ResponseEntity<Void> response = restTemplate.exchange(URL + "/" + categoryName, HttpMethod.PATCH, new HttpEntity<>(categoryDto), Void.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(categoryRepository.findCategoryByName(categoryName).isEmpty()).isTrue();
            assertThat(categoryRepository.findCategoryByName(afterName).isPresent()).isTrue();
        }


        @DisplayName("카테고리 수정 실패 - 이름 입력 X , 이미 존재하는 이름을 입력한 경우")
        @Test
        public void updateCategoryFail() {

            CategoryDto dto = new CategoryDto("사건");
            //이름 입력x
            CategoryDto wrongDto1 = new CategoryDto();
            //이미 존재하는 이름인 경우
            CategoryDto wrongDto2 = new CategoryDto(categoryName);

            ResponseEntity<List<ErrorDto>> response1 = restTemplate.exchange(URL + "/인물", HttpMethod.PATCH, new HttpEntity<>(wrongDto1), new ParameterizedTypeReference<List<ErrorDto>>() {
            });

            ResponseEntity<ErrorMsgDto> response2 = restTemplate.postForEntity(URL, wrongDto2, ErrorMsgDto.class);

            //존재하지 않는 카테고리를 변경하려 시도하는 경우
            ResponseEntity<ErrorMsgDto> response3 = restTemplate.exchange(URL + "/wrongName", HttpMethod.PATCH, new HttpEntity<>(dto), ErrorMsgDto.class);
            List<ErrorDto> body1 = response1.getBody();
            ErrorMsgDto body2 = response2.getBody();
            ErrorMsgDto body3 = response3.getBody();


            assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(body1.size()).isEqualTo(1);
            assertThat(body1.get(0)).usingRecursiveComparison().isEqualTo(new ErrorDto("name", "카테고리 이름을 입력해주세요"));

            assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(body2).usingRecursiveComparison().isEqualTo(new ErrorMsgDto("중복된 카테고리 이름입니다."));

            assertThat(response3.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(body3).usingRecursiveComparison().isEqualTo(new ErrorMsgDto("존재하지 않는 카테고리 제목입니다."));

            assertThat(categoryRepository.findAll().stream().map(c -> c.getName()).collect(Collectors.toSet()).containsAll(Set.of(categoryName)));
        }
    }


    @Nested
    @DisplayName("카테고리 삭제 - DELETE admin/categories")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    public class deleteCategory{

        @BeforeAll
        public void init(){
            suffix = "/admin/categories";
            initConfig();
        }

        @AfterEach
        public void clear() {
            topicRepository.deleteAllInBatch();
            chapterRepository.deleteAllInBatch();
            categoryRepository.deleteAllInBatch();
        }

        @BeforeEach
        public void setting() {
            baseSetting();
        }

        @DisplayName("카테고리 삭제 성공")
        @Test
        public void deleteCategorySuccess() {
            ResponseEntity<Void> response = restTemplate.exchange(URL + "/" + categoryName, HttpMethod.DELETE, null, Void.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(categoryRepository.findCategoryByName(categoryName).isEmpty()).isTrue();
        }

        @DisplayName("카테고리 삭제 실패 ")
        @Test
        public void deleteCategoryFail() {
            Chapter ch1 = new Chapter("ch1", 1);
            chapterRepository.saveAndFlush(ch1);

            Topic topic = new Topic("tite1", null, null, 0, 0, "detail1", ch1, c1);
            topicRepository.saveAndFlush(topic);

            //존재하지 않는 카테고리 삭제 시도
            ResponseEntity<ErrorMsgDto> response1 = restTemplate.exchange(URL + "/wrongName", HttpMethod.DELETE, null, ErrorMsgDto.class);

            //토픽이 존재하는 카테고리 삭제 시도
            ResponseEntity<ErrorMsgDto> response2 = restTemplate.exchange(URL + "/" + categoryName, HttpMethod.DELETE, null, ErrorMsgDto.class);

            assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response1.getBody()).usingRecursiveComparison().isEqualTo(new ErrorMsgDto("존재하지 않는 카테고리 제목입니다."));

            assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response2.getBody()).usingRecursiveComparison().isEqualTo(new ErrorMsgDto("해당 카테고리에 토픽이 존재합니다"));

            assertThat(categoryRepository.findCategoryByName(categoryName).isEmpty()).isFalse();
        }
    }



}