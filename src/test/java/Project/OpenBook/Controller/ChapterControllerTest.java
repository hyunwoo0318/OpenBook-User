package Project.OpenBook.Controller;


import Project.OpenBook.Domain.Category;
import Project.OpenBook.Domain.Chapter;
import Project.OpenBook.Domain.Topic;
import Project.OpenBook.Dto.chapter.ChapterDto;
import Project.OpenBook.Dto.chapter.ChapterListDto;
import Project.OpenBook.Dto.error.ErrorDto;
import Project.OpenBook.Dto.error.ErrorMsgDto;
import Project.OpenBook.Repository.category.CategoryRepository;
import Project.OpenBook.Repository.chapter.ChapterRepository;
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

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = { "spring.config.location=classpath:application-test.yml" })
class ChapterControllerTest {

    @LocalServerPort
    int port;
    @Autowired
    ChapterRepository chapterRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    TopicRepository topicRepository;
    @Autowired
    TestRestTemplate restTemplate;

    private Category c1;
    private Chapter ch1;
    private Topic t1;

    private final int chapterNum = 1;
    private final String prefix = "http://localhost:";
    private String suffix;

    String URL;

    private void initConfig() {
        URL = prefix + port + suffix;
        restTemplate = restTemplate.withBasicAuth("admin1", "admin1");
        restTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    }

    private void baseSetting() {
        c1 = new Category("유물");
        categoryRepository.saveAndFlush(c1);

        ch1 = new Chapter("ch1", chapterNum);
        chapterRepository.saveAndFlush(ch1);

        t1 = new Topic("title1", null, null, 0, 0, "detail1", ch1, c1);
        topicRepository.saveAndFlush(t1);
    }

    private void baseClear() {
        topicRepository.deleteAllInBatch();
        chapterRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
    }

    @Nested
    @DisplayName("단원 조회 - GET /admin/chapters")
    @TestInstance(PER_CLASS)
    public class queryChapters{

        @BeforeAll
        public void init(){
            suffix = "/admin/chapters";
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


        @DisplayName("단원 전체 조회 성공")
        @Test
        public void queryChaptersSuccess() {
            List<Chapter> chapterList = new ArrayList<>();
            List<ChapterDto> expectBody = new ArrayList<>();
            expectBody.add(new ChapterDto("ch1", 1));
            for (int i = 2; i <= 5; i++) {
                Chapter c = new Chapter("title" + i, i);
                expectBody.add(new ChapterDto("title" + i, i));
                chapterList.add(c);
            }
            chapterRepository.saveAllAndFlush(chapterList);
            chapterList.add(ch1);

            ResponseEntity<List<ChapterDto>> response = restTemplate.exchange(URL, HttpMethod.GET, null, new ParameterizedTypeReference<List<ChapterDto>>() {
            });

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(expectBody);
        }
    }

    //TODO : 특정 단원의 모든 토픽 조회기능 테스트 구현

    @Nested
    @DisplayName("단원 저장- POST /admin/chapters")
    @TestInstance(PER_CLASS)
    public class createChapter{
        @BeforeAll
        public void init(){
            suffix = "/admin/chapters";
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

        @DisplayName("새로운 단원 저장 성공 - POST /admin/chapters")
        @Test
        public void createNewChapterSuccess() {
            ChapterDto inputChapterDto = new ChapterDto("ch2", 2);
            ResponseEntity<Void> response = restTemplate.postForEntity(URL, inputChapterDto,Void.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            List<Chapter> chapterList = chapterRepository.findAll();
            assertThat(chapterList.size()).isEqualTo(2);
        }


        @DisplayName("잘못된 입력으로 단원 저장 실패 - POST /admin/chapters")
        @Test
        public void createNewChapterFail() {

            //제목을 입력하지 않은 경우
            ChapterDto wrongDto1 = new ChapterDto();
            wrongDto1.setNumber(2);

            //번호를 입력하지 않은 경우
            ChapterDto wrongDto2 = new ChapterDto();
            wrongDto2.setTitle("title123");

            //중복된 단원번호를 사용하는 경우
            ChapterDto wrongDto3 = new ChapterDto("title123", 1);

            ResponseEntity<List<ErrorDto>> response1 = restTemplate.exchange(URL, HttpMethod.POST, new HttpEntity<>(wrongDto1), new ParameterizedTypeReference<List<ErrorDto>>() {});
            ResponseEntity<List<ErrorDto>> response2 = restTemplate.exchange(URL, HttpMethod.POST, new HttpEntity<>(wrongDto2), new ParameterizedTypeReference<List<ErrorDto>>() {});

            ResponseEntity<ErrorMsgDto> response3 = restTemplate.postForEntity(URL, wrongDto3, ErrorMsgDto.class);

            List<ErrorDto> body1 = response1.getBody();
            List<ErrorDto> body2 = response2.getBody();
            ErrorMsgDto body3 = response3.getBody();


            assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(body1.size()).isEqualTo(1);
            assertThat(body1.get(0)).usingRecursiveComparison().isEqualTo(new ErrorDto("title", "단원제목을 입력해주세요."));

            assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(body2.size()).isEqualTo(1);
            assertThat(body2.get(0)).usingRecursiveComparison().isEqualTo(new ErrorDto("number", "단원 번호를 입력해주세요."));

            assertThat(response3.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(body3).usingRecursiveComparison().isEqualTo(new ErrorMsgDto("중복된 단원 번호입니다."));
        }

    }




    @Nested
    @DisplayName("단원 수정 - PATCH /admin/chapters")
    @TestInstance(PER_CLASS)
    public class updateChapter{
        @BeforeAll
        public void init(){
            suffix = "/admin/chapters";
            initConfig();
        }

        @AfterEach
        public void clear(){
            baseClear();
        }

        @BeforeEach
        public void setting() {
            baseSetting();
            Chapter ch2 = new Chapter("ch2", 2);
            chapterRepository.saveAndFlush(ch2);
        }

        @DisplayName("단원 수정 성공")
        @Test
        public void updateChapterSuccess() {
            ChapterDto inputDto = new ChapterDto("titleAfterUpdate", 3);

            ResponseEntity<Void> response = restTemplate.exchange(URL + "/" + chapterNum, HttpMethod.PATCH, new HttpEntity<>(inputDto), Void.class);

            Chapter afterChapter = chapterRepository.findOneByNumber(3).get();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(chapterRepository.findOneByNumber(1).isEmpty()).isTrue();
            assertThat(afterChapter.getTitle()).isEqualTo("titleAfterUpdate");
        }

        @DisplayName("단원 수정 실패 - PATCH /admin/chapters")
        @Test
        public void updateChapterFail(){
            //단원번호를 입력하지않음
            ChapterDto wrongDto1 = new ChapterDto();
            wrongDto1.setTitle("title123");

            //제목을 입력하지않음
            ChapterDto wrongDto2 = new ChapterDto();
            wrongDto2.setNumber(2);

            ChapterDto dto3 = new ChapterDto("title123", 5);
            ChapterDto dto4 = new ChapterDto("title123", 2);

            ResponseEntity<List<ErrorDto>> response1 = restTemplate.exchange(URL + "/" + chapterNum, HttpMethod.PATCH, new HttpEntity<>(wrongDto1), new ParameterizedTypeReference<List<ErrorDto>>() {});
            ResponseEntity<List<ErrorDto>> response2 = restTemplate.exchange(URL + "/" + chapterNum, HttpMethod.PATCH, new HttpEntity<>(wrongDto2), new ParameterizedTypeReference<List<ErrorDto>>() {});

            //존재하지 않는 단원 수정 요청
            ResponseEntity<ErrorMsgDto> response3 = restTemplate.exchange(URL + "/-1", HttpMethod.PATCH, new HttpEntity<>(dto3), ErrorMsgDto.class);

            //이미 존재하는 단원 번호로 수정 요청
            ResponseEntity<ErrorMsgDto> response4 = restTemplate.exchange(URL + "/" + chapterNum, HttpMethod.PATCH, new HttpEntity<>(dto4), ErrorMsgDto.class);

            List<ErrorDto> body1 = response1.getBody();
            List<ErrorDto> body2 = response2.getBody();
            ErrorMsgDto body3 = response3.getBody();
            ErrorMsgDto body4 = response4.getBody();

            assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(body1.size()).isEqualTo(1);
            assertThat(body1.get(0)).usingRecursiveComparison().isEqualTo(new ErrorDto("number", "단원 번호를 입력해주세요."));

            assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(body2.size()).isEqualTo(1);
            assertThat(body2.get(0)).isEqualTo(new ErrorDto("title", "단원제목을 입력해주세요."));

            assertThat(response3.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(body3).usingRecursiveComparison().isEqualTo(new ErrorMsgDto("존재하지 않는 단원 번호입니다."));

            assertThat(response4.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(body4).usingRecursiveComparison().isEqualTo(new ErrorMsgDto("중복된 단원 번호입니다."));
        }
    }


    @Nested
    @DisplayName("단원 삭제 - DELETE admin/chapters")
    @TestInstance(PER_CLASS)
    public class deleteChapter {
        @BeforeAll
        public void init(){
            suffix = "/admin/chapters";
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


        @DisplayName("단원 삭제 성공")
        @Test
        public void deleteChapterSuccess() {
            topicRepository.deleteAllInBatch();
            ResponseEntity<Void> response = restTemplate.exchange(URL + "/"+ chapterNum, HttpMethod.DELETE, null, Void.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(chapterRepository.findOneByNumber(chapterNum).isEmpty()).isTrue();
        }

        @DisplayName("단원 삭제 실패 - DELETE admin/chapters")
        @Test
        public void deleteChapterFail() {
            //존재하지 않는 단원 삭제 시도
            ResponseEntity<ErrorMsgDto> response1 = restTemplate.exchange(URL + "/-1", HttpMethod.DELETE, null, ErrorMsgDto.class);

            //토픽이 존재하는 단원 삭제 시도
            ResponseEntity<ErrorMsgDto> response2 = restTemplate.exchange(URL + "/" + chapterNum, HttpMethod.DELETE, null, ErrorMsgDto.class);

            assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response1.getBody()).usingRecursiveComparison().isEqualTo(new ErrorMsgDto("존재하지 않는 단원 번호입니다."));

            assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response2.getBody()).usingRecursiveComparison().isEqualTo(new ErrorMsgDto("해당 단원에 토픽이 존재합니다."));
            assertThat(chapterRepository.findOneByNumber(1).isPresent()).isTrue();
        }


    }


}