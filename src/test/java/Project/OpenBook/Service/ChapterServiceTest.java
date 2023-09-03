package Project.OpenBook.Service;

import Project.OpenBook.Constants.ContentConst;
import Project.OpenBook.Constants.ErrorCode;
import Project.OpenBook.Domain.*;
import Project.OpenBook.Dto.chapter.ChapterDto;
import Project.OpenBook.Dto.chapter.ChapterInfoDto;
import Project.OpenBook.Dto.chapter.ChapterUserDto;
import Project.OpenBook.Dto.studyProgress.ProgressDto;
import Project.OpenBook.Dto.topic.ChapterAdminDto;
import Project.OpenBook.Repository.chapter.ChapterRepository;
import Project.OpenBook.Repository.chapterprogress.ChapterProgressRepository;
import Project.OpenBook.Repository.chaptersection.ChapterSectionRepository;
import Project.OpenBook.Repository.topic.TopicRepository;
import Project.OpenBook.Repository.topicprogress.TopicProgressRepository;
import Project.OpenBook.Utils.CustomException;
import com.querydsl.core.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.Collectors;

import static Project.OpenBook.Constants.ContentConst.*;
import static Project.OpenBook.Constants.StateConst.LOCKED;
import static Project.OpenBook.Constants.StateConst.OPEN;
import static Project.OpenBook.Domain.QChoice.choice;
import static Project.OpenBook.Domain.QDescription.description;
import static Project.OpenBook.Domain.QKeyword.keyword;
import static Project.OpenBook.Domain.QTopic.topic;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChpaterService Class")
public class ChapterServiceTest {

    @InjectMocks
    private ChapterService chapterService;

    @Mock
    private ChapterRepository chapterRepository;

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private ChapterProgressRepository chapterProgressRepository;
    @Mock
    private ChapterSectionRepository chapterSectionRepository;

    @Mock private TopicProgressRepository topicProgressRepository;

    @Nested
    @DisplayName("createChapter() 메서드는")
    public class createChatperTest{

        @Nested
        @DisplayName("단원 번호와 제목을 입력하면")
        public class inputChapterNumAndTitle{

            private final String chapterTitle = "ch1";
            private final Integer chapterNum = 1;
            @Test
            @DisplayName("단원을 DB에 저장하고 해당 단원을 리턴한다.")
            public void saveChapterAndReturn() {
                //given
                Chapter chapter = new Chapter(chapterTitle, chapterNum);
                given(chapterRepository.save(any()))
                        .willReturn(chapter);

                //when
                Chapter findChapter = chapterService.createChapter(chapterTitle, chapterNum);

                //then
                assertEquals(findChapter.getNumber(), chapterNum);
                assertEquals(findChapter.getTitle(), chapterTitle);
            }

            @Test
            @DisplayName("이미 존재하는 단원 번호를 입력하면 DUP_CHAPTER_NUM Exception을 날린다.")
            public void dupChapterNum() {
                //given
                Chapter chapter = new Chapter(chapterTitle, chapterNum);
                given(chapterRepository.findOneByNumber(chapterNum))
                        .willReturn(Optional.of(chapter));

                //when
                CustomException customException = assertThrows(CustomException.class, () ->
                        chapterService.createChapter(chapterTitle, chapterNum));
                //then
                assertEquals(customException.getErrorCode(), ErrorCode.DUP_CHAPTER_NUM);
            }
        }
    }

    @Nested
    @DisplayName("queryAllChapters() 메서드는")
    public class queryAllChaptersTest{

        @Nested
        @DisplayName("단원이 1개 이상 존재한다면")
        public class existChapters{
            @Test
            @DisplayName("전체 단원에 대한 ChapterDto를 리스트로 리턴한다.")
            public void returnChapterDtoList() {
                //given
                Chapter ch1 = new Chapter("ch1", 1);
                Chapter ch2 = new Chapter("ch2", 2);
                List<Chapter> chapterList = Arrays.asList(ch1, ch2);

                List<ChapterDto> expectChapterDtoList = chapterList.stream()
                        .map(c -> new ChapterDto(c.getTitle(), c.getNumber()))
                        .collect(Collectors.toList());

                given(chapterRepository.findAll())
                        .willReturn(chapterList);

                //when
                List<ChapterDto> chapterDtoList = chapterService.queryAllChapters();

                //then
                assertEquals(chapterDtoList, expectChapterDtoList);
            }
        }

        @Nested
        @DisplayName("단원이 존재하지 않는다면")
        public class notExistChapters{

            @Test
            @DisplayName("빈 리스트를 리턴한다.")
            public void returnEmptyList(){
                //given
                given(chapterRepository.findAll())
                        .willReturn(new ArrayList<>());

                //when
                List<ChapterDto> chapterDtoList = chapterService.queryAllChapters();

                //then
                assertTrue(chapterDtoList.isEmpty());
            }
        }

    }

    @Nested
    @DisplayName("queryTopicsInChapterAdmin() 메서드는")
    public class queryTopicsInChapterAdminTest{

        @Nested
        @DisplayName("존재하는 단원 번호를 입력하면")
        public class inputChapterNum{
            @Test
            @DisplayName("TopicAdminDto를 리턴한다.")
            public void returnTopicAdminDto() {
                //given
                ChapterAdminDto expectResult = new ChapterAdminDto("categoty1", "title1", 1234, 5678, 0L, 0L, 0L);
                Tuple tuple1 = mock(Tuple.class);

                given(tuple1.get(topic.category.name))
                        .willReturn(expectResult.getCategory());
                given(tuple1.get(topic.title))
                        .willReturn(expectResult.getTitle());
                given(tuple1.get(topic.startDate))
                        .willReturn(expectResult.getStartDate());
                given(tuple1.get(topic.endDate))
                        .willReturn(expectResult.getEndDate());
                given(tuple1.get(description.countDistinct()))
                        .willReturn(expectResult.getDescriptionCount());
                given(tuple1.get(choice.countDistinct()))
                        .willReturn(expectResult.getChoiceCount());
                given(tuple1.get(keyword.countDistinct()))
                        .willReturn(expectResult.getKeywordCount());

                given(chapterRepository.findOneByNumber(anyInt()))
                        .willReturn(Optional.ofNullable(new Chapter("ch1", 1)));
                given(topicRepository.queryAdminChapterDto(any()))
                        .willReturn(Arrays.asList(tuple1));

                //when
                List<ChapterAdminDto> chapterAdminDtoList = chapterService.queryTopicsInChapterAdmin(1);

                //then
                assertEquals(chapterAdminDtoList, Arrays.asList(expectResult));
            }
        }

        @Nested
        @DisplayName("존재하지 않는 단원 번호를 입력하면")
        public class inputNotExistChapterNum{
            @Test
            @DisplayName("CHAPTER_NOT_FOUND Exception을 던진다.")
            public void throwChapterNotFoundException() {

                //given
                given(chapterRepository.findOneByNumber(-1))
                        .willReturn(Optional.ofNullable(null));

                //when
                CustomException customException = assertThrows(CustomException.class, () ->
                        chapterService.queryChapterInfoAdmin(-1));

                //then
                assertEquals(customException.getErrorCode(), ErrorCode.CHAPTER_NOT_FOUND);
            }

        }
    }

    @Nested
    @DisplayName("updateChapter() 메서드는")
    public class updateChapterTest{

        @Nested
        @DisplayName("해당 단원의 단원번호와 변경할 단원 번호와 제목을 입력받으면")
        public class inputChapterUpdateDto{

            @Test
            @DisplayName("변경할 단원번호와 제목으로 단원 정보가 변경되고 변경된 단원이 리턴된다.")
            public void returnUpdatedChapter() {
                //given
                Integer prevChapterNum = 1;
                String newChapterTitle = "newChapterTitle";
                Integer newChapterNum = 2;
                Chapter prevChapter = new Chapter("ch1", prevChapterNum);

                given(chapterRepository.findOneByNumber(prevChapterNum))
                        .willReturn(Optional.ofNullable(prevChapter));
                //변경할 단원번호를 가진 단원이 없는 경우
                given(chapterRepository.findOneByNumber(newChapterNum))
                        .willReturn(Optional.ofNullable(null));

                //when
                Chapter updatedChapter = chapterService.updateChapter(1, newChapterTitle, newChapterNum);

                //then
                assertEquals(updatedChapter.getTitle(), newChapterTitle);
                assertEquals(updatedChapter.getNumber(), newChapterNum);
            }

            @Nested
            @DisplayName("변경하려는 단원번호를 가진 다른 단원이 존재한다면")
            public class dupChpaterNum{
                @Test
                @DisplayName("DUP_CHAPTER_NUM Exception을 던진다.")
                public void throwDupChapterNumException() {
                    //given
                    Integer prevChapterNum = 1;
                    String newChapterTitle = "newChapterTitle";
                    Integer newChapterNum = 2;
                    Chapter chapter = new Chapter("tite2", newChapterNum);

                    given(chapterRepository.findOneByNumber(2))
                            .willReturn(Optional.ofNullable(chapter));

                    //when
                    CustomException customException = assertThrows(CustomException.class, () ->
                            chapterService.updateChapter(prevChapterNum, newChapterTitle, newChapterNum));

                    //then
                    assertEquals(customException.getErrorCode(), ErrorCode.DUP_CHAPTER_NUM);
                }
            }
        }

    }

    @Nested
    @DisplayName("deleteChapter() 메서드는")
    public class deleteChapterTest{

        @Nested
        @DisplayName("존재하는 단원 번호를 입력하는 경우")
        public class existChapterNum{

            @Nested
            @DisplayName("해당 단원에 토픽이 존재하지 않는 경우")
            public class notExistTopic{

                @Test
                @DisplayName("해당 단원을 정상적으로 삭제하고 True를 리턴한다.")
                public void deleteChapterSuccess(){
                    //given
                    int num = 1;
                    Chapter ch1 = new Chapter("ch1", num);
                    given(chapterRepository.findOneByNumber(1)).willReturn(Optional.ofNullable(ch1));
                    given(topicRepository.findAllByChapter(ch1)).willReturn(new ArrayList<>());

                    //when
                    Boolean ret = chapterService.deleteChapter(num);

                    //then
                    assertThat(ret).isTrue();
                }

            }

            @Nested
            @DisplayName("해당 단원에 토픽이 존재하는 경우")
            public class existTopic{

                @Test
                @DisplayName("CHAPTER_HAS_TOPIC exception을 날린다.")
                public void throwChapterHasTopicException(){
                    //given
                    int num = 1;
                    Chapter ch1 = new Chapter("ch1", num);
                    given(chapterRepository.findOneByNumber(1)).willReturn(Optional.ofNullable(ch1));
                    Topic topic = new Topic("t1", 0, 0, false, false, 0, 0, "detail1", ch1, null);
                    given(topicRepository.findAllByChapter(ch1)).willReturn(Arrays.asList(topic));

                    //when
                    CustomException customException = assertThrows(CustomException.class, () -> {
                        chapterService.deleteChapter(num);
                    });

                    //then
                    assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.CHAPTER_HAS_TOPIC);
                }
            }
        }

        @Nested
        @DisplayName("존재하지 않는 단원 번호를 입력하는 경우")
        public class notExistChapterNum{

            @Test
            @DisplayName("CHAPTER_NOT_FOUND Exception을 날린다.")
            public void throwChapterNotFoundException(){
                //given
                int num = 1;
                given(chapterRepository.findOneByNumber(num)).willReturn(Optional.empty());

                //when
                CustomException customException = assertThrows(CustomException.class, () -> {
                    chapterService.deleteChapter(num);
                });

                //then
                assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.CHAPTER_NOT_FOUND);
            }
        }
    }

    @Nested
    @DisplayName("updateChapterInfo() 메서드는")
    public class updateChapterInfoTest{

        @Nested
        @DisplayName("존재하는 단원 번호를 입력하는 경우")
        public class existChapterNum{

            @Test
            @DisplayName("입력한 content로 chapterInfo를 변경하고 변경한 chapterInfo로 만든 chapterInfoDto를 리턴한다.")
            public void returnChapterInfoDto(){
                //given
                String inputContent = "new Content";
                int num = 1;
                Chapter ch1 = new Chapter("ch1", 1);
                given(chapterRepository.findOneByNumber(num)).willReturn(Optional.ofNullable(ch1));

                //when
                ChapterInfoDto chapterInfoDto = chapterService.updateChapterInfo(num, inputContent);

                //then
                assertThat(chapterInfoDto).isEqualTo(new ChapterInfoDto(inputContent));
            }
        }

        @Nested
        @DisplayName("존재하지 않는 단원 번호를 입력하는 경우")
        public class notExistChapterNum{

            @Test
            @DisplayName("CHAPTER_NOT_FOUND Exception을 날린다.")
            public void throwChapterNotFoundException(){
                //given
                int num = 1;
                given(chapterRepository.findOneByNumber(num)).willReturn(Optional.empty());

                //when
                CustomException customException = assertThrows(CustomException.class, () -> {
                    chapterService.updateChapterInfo(num, "input");
                });

                //then
                assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.CHAPTER_NOT_FOUND);
            }
        }
    }

    @Nested
    @DisplayName("queryChapterUserDtos() 메서드는")
    public class queryChapterUserDtosTest{

        @Nested
        @DisplayName("각 단원별 chapterProgress가 존재한다면")
        public class existChapterProgress{

            @Test
            @DisplayName("{단원 제목, 단원 번호, state, progress}의 리스트를 리턴한다.")
            public void returnChapterUserDtos(){
                //given
                Customer mockCustomer = mock(Customer.class);
                when(mockCustomer.getId()).thenReturn(1L);

                Chapter ch1 = new Chapter("ch1", 1);
                Chapter ch2 = new Chapter("ch2", 2);
                Chapter ch3 = new Chapter("ch3", 3);

                ChapterProgress cp1 = new ChapterProgress(mockCustomer, ch1, 0, COMPLETE.getName());
                ChapterProgress cp2 = new ChapterProgress(mockCustomer, ch2, 0, ContentConst.CHAPTER_INFO.getName());
                ChapterProgress cp3 = new ChapterProgress(mockCustomer, ch3, 0, ContentConst.NOT_STARTED.getName());

                Map<Chapter, ChapterProgress> map = new HashMap<>();
                map.put(ch1, cp1);
                map.put(ch2, cp2);
                map.put(ch3, cp3);


                given(chapterRepository.queryChapterWithProgress(1L)).willReturn(map);

                //when
                List<ChapterUserDto> chapterUserDtoList = chapterService.queryChapterUserDtos(mockCustomer);

                //then
                ChapterUserDto dto1 = new ChapterUserDto(ch1.getTitle(), ch1.getNumber(), OPEN.getName(), COMPLETE.getName());
                ChapterUserDto dto2 = new ChapterUserDto(ch2.getTitle(), ch2.getNumber(), OPEN.getName(), CHAPTER_INFO.getName());
                ChapterUserDto dto3 = new ChapterUserDto(ch3.getTitle(), ch3.getNumber(), LOCKED.getName(), NOT_STARTED.getName());

                //단원 순대로 정렬까지 되었는지 확인
                assertThat(chapterUserDtoList).usingRecursiveComparison().isEqualTo(Arrays.asList(dto1, dto2, dto3));
            }
        }

        @Nested
        @DisplayName("각 단원별 chapterProgress가 존재하지 않는다면")
        public class notExistChapterProgress{

            @Test
            @DisplayName("각 단원별 chapterProgress를 생성하고 {단원 제목, 단원 번호, state, progress}의 리스트를 리턴한다.")
            public void createChapterProgress_returnChapterUserDtos() {
                //given
                Customer mockCustomer = mock(Customer.class);
                when(mockCustomer.getId()).thenReturn(1L);

                Chapter ch1 = new Chapter("ch1", 1);
                Chapter ch2 = new Chapter("ch2", 2);

                Map<Chapter, ChapterProgress> map = new HashMap<>();
                map.put(ch1, null);
                map.put(ch2, null);

                given(chapterRepository.queryChapterWithProgress(1L)).willReturn(map);


                //when
                List<ChapterUserDto> chapterUserDtoList = chapterService.queryChapterUserDtos(mockCustomer);

                //then

                //1단원은 state = open, progress = chapter_info
                ChapterUserDto dto1 = new ChapterUserDto(ch1.getTitle(), ch1.getNumber(), OPEN.getName(), CHAPTER_INFO.getName());
                //2단원 이후는 state = locked, progress = not_started
                ChapterUserDto dto2 = new ChapterUserDto(ch2.getTitle(), ch2.getNumber(), LOCKED.getName(), NOT_STARTED.getName());

                assertThat(chapterUserDtoList).usingRecursiveComparison().isEqualTo(Arrays.asList(dto1, dto2));
            }
        }
    }

    @Nested
    @DisplayName("queryContentTable() 메서드는")
    public class queryContentTableTest {

        @Nested
        @DisplayName("각 content마다 chapterSection이 존재하는 경우")
        public class existChapterSection{
            @Nested
            @DisplayName("해당 단원에 topic이 존재하는 경우")
            public class existTopic{

                @Test
                @DisplayName("단원학습 -> 연표학습 -> [{주제 학습}] -> 단원 마무리 학습의 순서를 가지는 목차를 리턴한다.")
                public void returnContentTable(){
                    //given
                    String chapterTitle = "ch1";
                    Chapter ch1 = new Chapter(chapterTitle, 1);
                    Customer mockCustomer = mock(Customer.class);
                    when(mockCustomer.getId()).thenReturn(1L);
                    given(chapterRepository.findOneByNumber(1)).willReturn(Optional.ofNullable(ch1));
                    ChapterSection cs1 = new ChapterSection(mockCustomer, ch1, CHAPTER_INFO.getName(), OPEN.getName());
                    ChapterSection cs2 = new ChapterSection(mockCustomer, ch1, TIME_FLOW_STUDY.getName(), OPEN.getName());
                    ChapterSection cs3 = new ChapterSection(mockCustomer, ch1, CHAPTER_COMPLETE_QUESTION.getName(), LOCKED.getName());
                    given(chapterSectionRepository.queryChapterSections(1L, 1))
                            .willReturn(Arrays.asList(cs1, cs2, cs3));

                    Topic topic1 = new Topic("t1", 0, 0, false, false, 0, 0, "1", ch1, null);
                    TopicProgress topicProgress = new TopicProgress(mockCustomer, topic1, 0, OPEN.getName());
                    given(topicProgressRepository.queryTopicProgresses(1L, 1))
                            .willReturn(Arrays.asList(topicProgress));

                    //when
                    List<ProgressDto> contentTableList = chapterService.queryContentTable(mockCustomer, 1);

                    //then
                    ProgressDto dto1 = new ProgressDto(CHAPTER_INFO.getName(), chapterTitle, OPEN.getName());
                    ProgressDto dto2 = new ProgressDto(TIME_FLOW_STUDY.getName(), chapterTitle, OPEN.getName());
                    ProgressDto dto3 = new ProgressDto(TOPIC_STUDY.getName(), topic1.getTitle(), OPEN.getName());
                    ProgressDto dto4 = new ProgressDto(CHAPTER_COMPLETE_QUESTION.getName(), chapterTitle, LOCKED.getName());

                    assertThat(contentTableList).usingRecursiveComparison().isEqualTo(Arrays.asList(dto1, dto2, dto3, dto4));
                }
            }

            @Nested
            @DisplayName("해당 단원에 topic이 존재하지 않는 경우")
            public class notExistTopic{

                @Test
                @DisplayName("단원 학습 -> 연표 문제 -> 단원 마무리 문제로 구성된 목차 리턴")
                public void returnContentTableWithoutTopic(){
                    //given
                    String chapterTitle = "ch1";
                    Chapter ch1 = new Chapter(chapterTitle, 1);
                    Customer mockCustomer = mock(Customer.class);
                    when(mockCustomer.getId()).thenReturn(1L);
                    given(chapterRepository.findOneByNumber(1)).willReturn(Optional.ofNullable(ch1));
                    ChapterSection cs1 = new ChapterSection(mockCustomer, ch1, CHAPTER_INFO.getName(), OPEN.getName());
                    ChapterSection cs2 = new ChapterSection(mockCustomer, ch1, TIME_FLOW_STUDY.getName(), OPEN.getName());
                    ChapterSection cs3 = new ChapterSection(mockCustomer, ch1, CHAPTER_COMPLETE_QUESTION.getName(), LOCKED.getName());
                    given(chapterSectionRepository.queryChapterSections(1L, 1))
                            .willReturn(Arrays.asList(cs1, cs2, cs3));


                    given(topicProgressRepository.queryTopicProgresses(1L, 1))
                            .willReturn(new ArrayList<>());

                    //when
                    List<ProgressDto> contentTableList = chapterService.queryContentTable(mockCustomer, 1);

                    //then
                    ProgressDto dto1 = new ProgressDto(CHAPTER_INFO.getName(), chapterTitle, OPEN.getName());
                    ProgressDto dto2 = new ProgressDto(TIME_FLOW_STUDY.getName(), chapterTitle, OPEN.getName());
                    ProgressDto dto3 = new ProgressDto(CHAPTER_COMPLETE_QUESTION.getName(), chapterTitle, LOCKED.getName());

                    assertThat(contentTableList).usingRecursiveComparison().isEqualTo(Arrays.asList(dto1, dto2, dto3));
                }
            }
        }

        @Nested
        @DisplayName("각 content마다 chapterSection이 존재하지 않는 경우")
        public class notExistChapterSection{
            @Nested
            @DisplayName("1단원의 경우")
            public class firstChapter{

                @Test
                @DisplayName("단원 학습은 open하고 나머지는 locked로 만들고 목차를 리턴한다.")
                public void openChapterInfo_createChapterSection_returnContentTable(){
                    //given
                    String chapterTitle = "ch1";
                    Chapter ch1 = new Chapter(chapterTitle, 1);
                    Customer mockCustomer = mock(Customer.class);
                    when(mockCustomer.getId()).thenReturn(1L);
                    given(chapterRepository.findOneByNumber(1)).willReturn(Optional.ofNullable(ch1));
                    given(chapterSectionRepository.queryChapterSections(1L, 1))
                            .willReturn(new ArrayList<>());


                    given(topicProgressRepository.queryTopicProgresses(1L, 1))
                            .willReturn(new ArrayList<>());

                    //when
                    List<ProgressDto> contentTableList = chapterService.queryContentTable(mockCustomer, 1);

                    //then
                    ProgressDto dto1 = new ProgressDto(CHAPTER_INFO.getName(), chapterTitle, OPEN.getName());
                    ProgressDto dto2 = new ProgressDto(TIME_FLOW_STUDY.getName(), chapterTitle, LOCKED.getName());
                    ProgressDto dto3 = new ProgressDto(CHAPTER_COMPLETE_QUESTION.getName(), chapterTitle, LOCKED.getName());

                    assertThat(contentTableList).usingRecursiveComparison().isEqualTo(Arrays.asList(dto1, dto2, dto3));

                }
            }

            @Nested
            @DisplayName("2단원 이후의 단원의 경우")
            public class exceptFirstChapter{

                @Test
                @DisplayName("전체 section을 locked로 생성하고 목차 리턴")
                public void createChapterSectionInLocked_returnContentTable(){
                    //given
                    String chapterTitle = "ch1";
                    Chapter ch1 = new Chapter(chapterTitle, 2);
                    Customer mockCustomer = mock(Customer.class);
                    when(mockCustomer.getId()).thenReturn(1L);
                    given(chapterRepository.findOneByNumber(2)).willReturn(Optional.ofNullable(ch1));
                    given(chapterSectionRepository.queryChapterSections(1L, 2))
                            .willReturn(new ArrayList<>());


                    given(topicProgressRepository.queryTopicProgresses(1L, 2))
                            .willReturn(new ArrayList<>());

                    //when
                    List<ProgressDto> contentTableList = chapterService.queryContentTable(mockCustomer, 2);

                    //then
                    ProgressDto dto1 = new ProgressDto(CHAPTER_INFO.getName(), chapterTitle, LOCKED.getName());
                    ProgressDto dto2 = new ProgressDto(TIME_FLOW_STUDY.getName(), chapterTitle, LOCKED.getName());
                    ProgressDto dto3 = new ProgressDto(CHAPTER_COMPLETE_QUESTION.getName(), chapterTitle, LOCKED.getName());

                    assertThat(contentTableList).usingRecursiveComparison().isEqualTo(Arrays.asList(dto1, dto2, dto3));
                }
            }
        }

        @Nested
        @DisplayName("존재하지 않는 단원번호를 입력하는 경우")
        public class notExistChapterNum{
            @Test
            @DisplayName("CHAPTER_NOT_FOUND Exception을 날림")
            public void throwChapterNotFoundException(){
                //given
                int num = 1;
                given(chapterRepository.findOneByNumber(num)).willReturn(Optional.empty());
                Customer mockCustomer = mock(Customer.class);

                //when
                CustomException customException = assertThrows(CustomException.class, () -> {
                    chapterService.queryContentTable(mockCustomer, num);
                });

                //then
                assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.CHAPTER_NOT_FOUND);
            }
        }
    }
}
