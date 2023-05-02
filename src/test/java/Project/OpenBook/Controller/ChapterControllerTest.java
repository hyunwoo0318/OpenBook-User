package Project.OpenBook.Controller;


import Project.OpenBook.Domain.Category;
import Project.OpenBook.Domain.Chapter;
import Project.OpenBook.Dto.chapter.ChapterDto;
import Project.OpenBook.Dto.chapter.ChapterListDto;
import Project.OpenBook.Dto.error.ErrorDto;
import Project.OpenBook.Repository.CategoryRepository;
import Project.OpenBook.Repository.ChapterRepository;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

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


    private final String prefix = "http://localhost:";
    private final String surfix = "/admin/chapters";

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

    @DisplayName("단원 전체 조회 성공 - GET /admin/chapters")
    @Test
    public void queryChaptersSuccess() {
        List<Chapter> chapterList = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Chapter c = new Chapter("title" + i, i);
            chapterList.add(c);
        }
        chapterRepository.saveAllAndFlush(chapterList);

        ResponseEntity<ChapterListDto> response = restTemplate.getForEntity(URL, ChapterListDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getTitleList()).isEqualTo(Arrays.asList("title1","title2","title3","title4","title5"));
        assertThat(response.getBody().getNumberList()).isEqualTo(Arrays.asList(1,2,3,4,5));

    }


    @DisplayName("새로운 단원 저장 성공 - POST /admin/chapters")
    @Test
    public void createNewChapterSuccess() {
        ChapterDto inputChapterDto = new ChapterDto("title1", 1);

        ResponseEntity<Void> response = restTemplate.postForEntity(URL, inputChapterDto,Void.class);

        Optional<Chapter> chapterOptional = chapterRepository.findOneByNumber(1);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(chapterOptional.isPresent()).isTrue();
        assertThat(chapterOptional.get().getTitle()).isEqualTo("title1");
    }


    @DisplayName("잘못된 입력으로 단원 저장 실패 - POST /admin/chapters")
    @Test
    public void createNewChapterFail() {
        chapterRepository.saveAndFlush(new Chapter("title1", 1));

        //제목을 입력하지 않은 경우
        ChapterDto wrongChapterDto1 = new ChapterDto();
        wrongChapterDto1.setNumber(2);

        ChapterDto wrongChapterDto2 = new ChapterDto();
        wrongChapterDto2.setTitle("title123");

        //중복된 단원번호를 사용하는 경우
        ChapterDto wrongChapterDto3 = new ChapterDto("title123", 1);

        ResponseEntity<List<ErrorDto>> response1 = restTemplate.exchange(URL, HttpMethod.POST, new HttpEntity<>(wrongChapterDto1), new ParameterizedTypeReference<List<ErrorDto>>() {});
        ResponseEntity<List<ErrorDto>> response2 = restTemplate.exchange(URL, HttpMethod.POST, new HttpEntity<>(wrongChapterDto2), new ParameterizedTypeReference<List<ErrorDto>>() {});
        ResponseEntity<List<ErrorDto>> response3 = restTemplate.exchange(URL, HttpMethod.POST, new HttpEntity<>(wrongChapterDto3), new ParameterizedTypeReference<List<ErrorDto>>() {});

        ErrorDto errorDto1 = response1.getBody().get(0);
        ErrorDto errorDto2 = response2.getBody().get(0);
        ErrorDto errorDto3 = response3.getBody().get(0);

        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(errorDto1).isEqualTo(new ErrorDto("title", "단원제목을 입력해주세요."));

        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(errorDto2).isEqualTo(new ErrorDto("number", "단원 번호를 입력해주세요."));

        assertThat(response3.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(errorDto3).isEqualTo(new ErrorDto("chapter", "이미 존재하는 단원 번호입니다. 다른 단원 번호를 입력해 주세요."));
    }


    @DisplayName("단원 수정 성공 - PATCH /admin/chapters")
    @Test
    public void updateChapterSuccess() {
        chapterRepository.saveAndFlush(new Chapter("titleBeforeUpdate", 1));
        ChapterDto inputDto = new ChapterDto("titleAfterUpdate", 2);


        ResponseEntity<Void> response = restTemplate.exchange(URL + "/1", HttpMethod.PATCH, new HttpEntity<>(inputDto), Void.class);

        Optional<Chapter> chapterOptional = chapterRepository.findOneByNumber(2);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(chapterRepository.findOneByNumber(1).isEmpty()).isTrue();
        assertThat(chapterOptional.isPresent()).isTrue();
        assertThat(chapterOptional.get().getTitle()).isEqualTo("titleAfterUpdate");
    }

    @DisplayName("단원 수정 실패 - PATCH /admin/chapters")
    @Test
    public void updateChapterFail(){
        chapterRepository.saveAndFlush(new Chapter("title1", 1));

        //단원번호를 입력하지않음
        ChapterDto wrongDto1 = new ChapterDto();
        wrongDto1.setTitle("title123");

        //제목을 입력하지않음
        ChapterDto wrongDto2 = new ChapterDto();
        wrongDto2.setNumber(2);

        ChapterDto dto3 = new ChapterDto("title123", 1);

        ResponseEntity<List<ErrorDto>> response1 = restTemplate.exchange(URL + "/1", HttpMethod.PATCH, new HttpEntity<>(wrongDto1), new ParameterizedTypeReference<List<ErrorDto>>() {});
        ResponseEntity<List<ErrorDto>> response2 = restTemplate.exchange(URL + "/1", HttpMethod.PATCH, new HttpEntity<>(wrongDto2), new ParameterizedTypeReference<List<ErrorDto>>() {});

        //존재하지 않는 단원 수정 요구
        ResponseEntity<List<ErrorDto>> response3 = restTemplate.exchange(URL + "/100", HttpMethod.PATCH, new HttpEntity<>(dto3), new ParameterizedTypeReference<List<ErrorDto>>() {});

        ErrorDto errorDto1 = response1.getBody().get(0);
        ErrorDto errorDto2 = response2.getBody().get(0);

        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(errorDto1).isEqualTo(new ErrorDto("number", "단원 번호를 입력해주세요."));

        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(errorDto2).isEqualTo(new ErrorDto("title", "단원제목을 입력해주세요."));

        assertThat(response3.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @DisplayName("단원 삭제 성공 - DELETE admin/chapters")
    @Test
    public void deleteChapterSuccess() {
        Category category = new Category("유물");
        Chapter chapter = new Chapter("ct1", 1111);
        chapterRepository.saveAndFlush(chapter);
        categoryRepository.saveAndFlush(category);

        ResponseEntity<Void> response = restTemplate.exchange(URL + "/1111", HttpMethod.DELETE, null, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(chapterRepository.findOneByNumber(1111).isEmpty()).isTrue();
    }

    @DisplayName("단원 삭제 실패 - DELETE admin/chapters")
    @Test
    public void deleteChapterFail() {
        chapterRepository.saveAndFlush(new Chapter("title1", 1));

        ResponseEntity<Void> response = restTemplate.exchange(URL + "/2", HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(chapterRepository.findOneByNumber(1).isPresent()).isTrue();
    }

}