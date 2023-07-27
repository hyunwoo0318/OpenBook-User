package Project.OpenBook.Controller;


import Project.OpenBook.Constants.ErrorCode;
import Project.OpenBook.Domain.*;
import Project.OpenBook.Dto.chapter.*;
import Project.OpenBook.Dto.error.ErrorDto;
import Project.OpenBook.Dto.error.ErrorMsgDto;
import Project.OpenBook.Dto.topic.AdminChapterDto;
import Project.OpenBook.Repository.category.CategoryRepository;
import Project.OpenBook.Repository.chapter.ChapterRepository;
import Project.OpenBook.Repository.choice.ChoiceRepository;
import Project.OpenBook.Repository.description.DescriptionRepository;
import Project.OpenBook.Repository.keyword.KeywordRepository;
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
import org.springframework.security.core.parameters.P;
import org.springframework.test.context.TestPropertySource;

import java.util.*;

import static Project.OpenBook.Constants.ErrorCode.*;
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
    KeywordRepository keywordRepository;

    @Autowired
    ChoiceRepository choiceRepository;

    @Autowired
    DescriptionRepository descriptionRepository;

    @Autowired
    TestRestTemplate restTemplate;

    private Category c1;
    private Chapter ch1;
    private Topic t1;

    private final int chapterNum = 1;
    private final String prefix = "http://localhost:";
    private String suffix;

    String URL;

    String chapterInfo;

    private void initConfig() {
        URL = prefix + port + suffix;
        restTemplate = restTemplate.withBasicAuth("admin1", "admin1");
        restTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    }

    private void baseSetting() {
        c1 = new Category("유물");
        categoryRepository.saveAndFlush(c1);

        chapterInfo = "chapterInfo!!";
        ch1 = new Chapter("ch1", chapterNum);
        ch1.updateContent(chapterInfo);
        chapterRepository.saveAndFlush(ch1);

        t1 = new Topic("title1", 1234, 2314, false,false,0, 0, "detail1", ch1, c1);
        topicRepository.saveAndFlush(t1);
    }

    private void baseClear() {
        topicRepository.deleteAllInBatch();
        chapterRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
    }

    @Nested
    @DisplayName("모든 단원 조회 - GET /admin/chapters")
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
            for (int i = 2; i <= 5; i++) {
                Chapter c = new Chapter("title" + i, i);
                chapterList.add(c);
            }
            chapterRepository.saveAllAndFlush(chapterList);
            chapterList.add(ch1);

            ResponseEntity<List<ChapterDto>> response = restTemplate.exchange(URL, HttpMethod.GET, null, new ParameterizedTypeReference<List<ChapterDto>>() {
            });

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().stream().map(c -> c.getNumber())).usingRecursiveComparison().isEqualTo(Arrays.asList(1, 2, 3, 4, 5));
        }
    }


    @Nested
    @DisplayName("단원 이름 조회 - GET /admin/chapters/chapter-title")
    @TestInstance(PER_CLASS)
    public class queryChapterTitle{

        @BeforeAll
        public void init(){
            suffix = "/admin/chapters/chapter-title";
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


        @DisplayName("단원 이름 조회 성공")
        @Test
        public void queryChaptersSuccess() {
            ResponseEntity<ChapterTitleDto> response = restTemplate.getForEntity(URL + "?num=1", ChapterTitleDto.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getTitle()).isEqualTo("ch1");

        }

        @DisplayName("단원 이름 조회 실패 - 존재하지 않는 단원번호 입력")
        @Test
        public void queryChaptersFail() {
            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL + "?num=-1", HttpMethod.GET,
                    null, new ParameterizedTypeReference<List<ErrorMsgDto>>() {});

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto("존재하지 않는 단원 번호입니다.")));
        }
    }

    @Nested
    @DisplayName("단원 이름과 단원 학습 조회 - GET /admin/chapters/title-info")
    @TestInstance(PER_CLASS)
    public class queryChapterTitleInfo{

        @BeforeAll
        public void init(){
            suffix = "/admin/chapters/title-info";
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


        @DisplayName("단원 이름과 단원 학습 조회 성공")
        @Test
        public void queryChapterTitleInfoSuccess() {
            ResponseEntity<ChapterTitleInfoDto> response = restTemplate.getForEntity(URL + "?num=1", ChapterTitleInfoDto.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getTitle()).isEqualTo(ch1.getTitle());
            assertThat(response.getBody().getContent()).isEqualTo(ch1.getContent());
        }

        @DisplayName("단원 이름과 단원 학습 조회 실패 - 존재하지 않는 단원번호 입력")
        @Test
        public void queryChapterTitleInfoFail() {
            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL + "?num=-1", HttpMethod.GET,
                    null, new ParameterizedTypeReference<List<ErrorMsgDto>>() {});

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto("존재하지 않는 단원 번호입니다.")));
        }
    }

    @Nested
    @DisplayName("단원 학습 조회 - GET /admin/chapters/{num}/info")
    @TestInstance(PER_CLASS)
    public class queryChapterInfo{

        @BeforeAll
        public void init(){
            suffix = "/admin/chapters/";
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


        @DisplayName("단원 학습 조회 성공")
        @Test
        public void queryChapterInfoSuccess() {
            ResponseEntity<ChapterInfoDto> response = restTemplate.getForEntity(URL + ch1.getNumber() + "/info", ChapterInfoDto.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getContent()).isEqualTo(ch1.getContent());
        }

        @DisplayName("단원 학습 조회 실패 - 존재하지 않는 단원번호 입력")
        @Test
        public void queryChapterInfoFail() {
            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL + "-111/info", HttpMethod.GET,
                    null, new ParameterizedTypeReference<List<ErrorMsgDto>>() {});

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto(CHAPTER_NOT_FOUND.getErrorMessage())));

        }
    }

    @Nested
    @DisplayName("해당 단원의 모든 토픽 조회 - GET /admin/chapters/{num}/topics")
    @TestInstance(PER_CLASS)
    public class queryChapterTopics{

        @BeforeAll
        public void init(){
            suffix = "/admin/chapters/";
            initConfig();
        }

        @AfterEach
        public void clear(){
            keywordRepository.deleteAllInBatch();
            descriptionRepository.deleteAllInBatch();
            choiceRepository.deleteAllInBatch();
            baseClear();
        }

        @BeforeEach
        public void setting() {
            baseSetting();

            //t1에 키워드, 선지, 보기를 각각 2개씩 추가
            Keyword k1 = new Keyword("k1","c1", t1);
            Keyword k2 = new Keyword("k2","c2",t1);
            keywordRepository.save(k1);
            keywordRepository.save(k2);

            Choice choice1 = new Choice("choice1", t1);
            Choice choice2 = new Choice("choice2", t1);
            choiceRepository.save(choice1);
            choiceRepository.save(choice2);

            Description des1 = new Description("des1", t1);
            Description des2 = new Description("des2", t1);
            descriptionRepository.save(des1);
            descriptionRepository.save(des2);
        }


        @DisplayName("해당 단원의 모든 토픽 조회 성공")
        @Test
        public void queryChaptersTopicSuccess() {
            ResponseEntity<List<AdminChapterDto>> response = restTemplate.exchange(URL + "1/topics", HttpMethod.GET, null, new ParameterizedTypeReference<List<AdminChapterDto>>() {
            });
            List<AdminChapterDto> body = response.getBody();;
            AdminChapterDto expectBody = new AdminChapterDto("유물", "title1", 1234, 2314, 2L, 2L, 2L);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(body).usingRecursiveComparison().isEqualTo(Arrays.asList(expectBody));
        }

        @DisplayName("해당 단원의 모든 토픽 조회 실패 - 존재하지 않는 단원번호 입력")
        @Test
        public void queryChaptersTopicFail() {
            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL + "-1/topics", HttpMethod.GET,
                    null, new ParameterizedTypeReference<List<ErrorMsgDto>>() {});

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto(CHAPTER_NOT_FOUND.getErrorMessage())));

        }

    }



    @Nested
    @DisplayName("단원 학습 수정- PATCH /admin/chapters/{num}/info")
    @TestInstance(PER_CLASS)
    public class updateChapterInfo{

        private int chapterNum;
        @BeforeAll
        public void init(){
            suffix = "/admin/chapters/";
            initConfig();
        }

        @AfterEach
        public void clear(){
            baseClear();
        }

        @BeforeEach
        public void setting() {
            baseSetting();
            chapterNum = ch1.getNumber();
        }

        @DisplayName("단원 학습 수정 성공")
        @Test
        public void updateChapterInfoSuccess() {
            ChapterInfoDto dto = new ChapterInfoDto("new Info");
            ResponseEntity<Void> response = restTemplate.exchange(URL + chapterNum + "/info", HttpMethod.PATCH,
                    new HttpEntity<>(dto), Void.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(chapterRepository.findOneByNumber(chapterNum).get().getContent()).isEqualTo(dto.getContent());
        }


        @DisplayName("단원 학습 수정 실패 - DTO Validation")
        @Test
        public void updateChapterInfoFailWrongDto() {
            //내용을 입력하지 않은 경우
            ChapterInfoDto wrongDto = new ChapterInfoDto();

            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL + chapterNum + "/info", HttpMethod.PATCH,
                    new HttpEntity<>(wrongDto), new ParameterizedTypeReference<List<ErrorMsgDto>>() {});

            List<ErrorMsgDto> body = response.getBody();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(body.size()).isEqualTo(1);
        }

        @DisplayName("단원 학습 수정 실패 - 존재하지 않는 단원 번호 입력")
        @Test
        public void updateChapterFailNotFoundNum(){
            ChapterInfoDto dto = new ChapterInfoDto("new Content");
            //존재하지 않는 단원 수정 요청
            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL + "-1/info", HttpMethod.PATCH,
                    new HttpEntity<>(dto), new ParameterizedTypeReference<List<ErrorMsgDto>>() {});

            List<ErrorMsgDto> body = response.getBody();

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(body).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto(CHAPTER_NOT_FOUND.getErrorMessage())));

        }
    }


    @Nested
    @DisplayName("단원 추가- POST /admin/chapters")
    @TestInstance(PER_CLASS)
    public class createChapter{

        private int prevSize;
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
            prevSize = chapterRepository.findAll().size();
        }

        @DisplayName("새로운 단원 저장 성공")
        @Test
        public void createNewChapterSuccess() {
            ChapterDto inputChapterDto = new ChapterDto("ch2", 2);
            ResponseEntity<Void> response = restTemplate.postForEntity(URL, inputChapterDto,Void.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(chapterRepository.findOneByNumber(2).isPresent()).isTrue();
            assertThat(chapterRepository.findAll().size()).isEqualTo(prevSize + 1);
        }


        @DisplayName("잘못된 입력으로 단원 저장 실패 - DTO Validation")
        @Test
        public void createChapterFailWrongDto() {
            //제목, 번호를 입력하지 않은 경우
            ChapterDto wrongDto = new ChapterDto();

            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL, HttpMethod.POST,
                    new HttpEntity<>(wrongDto), new ParameterizedTypeReference<List<ErrorMsgDto>>() {});

            List<ErrorMsgDto> body = response.getBody();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(body.size()).isEqualTo(2);

            assertThat(chapterRepository.findAll().size()).isEqualTo(prevSize);

        }

        @DisplayName("잘못된 입력으로 단원 저장 실패 - 중복된 단원번호를 입력한 경우")
        @Test
        public void createChapterFailDupNum() {
             //중복된 단원번호를 입력한 경우
            ChapterDto wrongDto = new ChapterDto("title123", 1);

            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL,HttpMethod.POST,
                    new HttpEntity<>(wrongDto),new ParameterizedTypeReference<List<ErrorMsgDto>>(){});

            List<ErrorMsgDto> body = response.getBody();

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(body).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto(DUP_CHAPTER_NUM.getErrorMessage())));

            assertThat(chapterRepository.findAll().size()).isEqualTo(prevSize);
        }

    }

    @Nested
    @DisplayName("단원 수정 - PATCH /admin/chapters")
    @TestInstance(PER_CLASS)
    public class updateChapter{

        private int prevSize;
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
            prevSize = chapterRepository.findAll().size();
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

            assertThat(chapterRepository.findAll().size()).isEqualTo(prevSize);
        }

        @DisplayName("단원 수정 실패 - DTO Validation")
        @Test
        public void updateChapterFailWrongDto(){
            //단원번호, 제목을 입력하지않음
            ChapterDto wrongDto = new ChapterDto();

            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL + "/" + chapterNum, HttpMethod.PATCH,
                    new HttpEntity<>(wrongDto), new ParameterizedTypeReference<List<ErrorMsgDto>>() {});

            List<ErrorMsgDto> body = response.getBody();

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(body.size()).isEqualTo(2);

            assertThat(chapterRepository.findAll().size()).isEqualTo(prevSize);
        }

        @DisplayName("단원 수정 실패 - 존재하지 않는 단원 번호 입력")
        @Test
        public void updateChapterFailNotFoundNum(){
            ChapterDto dto = new ChapterDto("title123", 5);

            //존재하지 않는 단원 수정 요청
            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL + "/-1", HttpMethod.PATCH,
                    new HttpEntity<>(dto), new ParameterizedTypeReference<List<ErrorMsgDto>>() {});

            List<ErrorMsgDto> body = response.getBody();

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(body).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto(CHAPTER_NOT_FOUND.getErrorMessage())));

            assertThat(chapterRepository.findAll().size()).isEqualTo(prevSize);
        }

        @DisplayName("단원 수정 실패 - 이미 존재하는 단원 번호로 수정 요청")
        @Test
        public void updateChapterFailDupNum(){
            ChapterDto dto = new ChapterDto("title123", 2);

            //존재하지 않는 단원 수정 요청
            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL + "/" + chapterNum, HttpMethod.PATCH,
                    new HttpEntity<>(dto), new ParameterizedTypeReference<List<ErrorMsgDto>>() {});

            List<ErrorMsgDto> body = response.getBody();

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(body).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto(DUP_CHAPTER_NUM.getErrorMessage())));

            assertThat(chapterRepository.findAll().size()).isEqualTo(prevSize);
        }
    }


    @Nested
    @DisplayName("단원 삭제 - DELETE admin/chapters")
    @TestInstance(PER_CLASS)
    public class deleteChapter {

        private int prevSize;
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
            prevSize = chapterRepository.findAll().size();
        }


        @DisplayName("단원 삭제 성공")
        @Test
        public void deleteChapterSuccess() {
            topicRepository.deleteAllInBatch();
            ResponseEntity<Void> response = restTemplate.exchange(URL + "/"+ chapterNum, HttpMethod.DELETE, null, Void.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(chapterRepository.findOneByNumber(chapterNum).isEmpty()).isTrue();

            assertThat(chapterRepository.findAll().size()).isEqualTo(prevSize-1);
        }

        @DisplayName("단원 삭제 실패 - 존재하지 않는 단원번호 입력")
        @Test
        public void deleteChapterFailNotFoundNum() {
            //존재하지 않는 단원번호 입력
            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL + "/-1", HttpMethod.DELETE,
                    null, new ParameterizedTypeReference<List<ErrorMsgDto>>() {});

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto(CHAPTER_NOT_FOUND.getErrorMessage())));

            assertThat(chapterRepository.findOneByNumber(1).isPresent()).isTrue();
            assertThat(chapterRepository.findAll().size()).isEqualTo(prevSize);
        }

        @DisplayName("단원 삭제 실패 - 토픽이 존재하는 단원 삭제 시도")
        @Test
        public void deleteChapterFailHasTopic() {
            //토픽이 존재하는 단원 삭제 시도
            ResponseEntity<List<ErrorMsgDto>> response = restTemplate.exchange(URL + "/" + chapterNum, HttpMethod.DELETE,
                    null, new ParameterizedTypeReference<List<ErrorMsgDto>>() {});

           assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(Arrays.asList(new ErrorMsgDto(CHAPTER_HAS_TOPIC.getErrorMessage())));

            assertThat(chapterRepository.findOneByNumber(1).isPresent()).isTrue();
            assertThat(chapterRepository.findAll().size()).isEqualTo(prevSize);
        }


    }


}