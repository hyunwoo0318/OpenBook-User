package Project.OpenBook.Service;

import Project.OpenBook.Chapter.Domain.Chapter;
import Project.OpenBook.Chapter.Service.ChapterWithProgressService;
import Project.OpenBook.Chapter.Controller.dto.ChapterUserDto;
import Project.OpenBook.Constants.ContentConst;
import Project.OpenBook.Constants.ErrorCode;
import Project.OpenBook.Domain.*;
import Project.OpenBook.Dto.studyProgress.ProgressDto;
import Project.OpenBook.Chapter.Repo.ChapterRepository;
import Project.OpenBook.Repository.chaptersection.ChapterSectionRepository;
import Project.OpenBook.Repository.topicprogress.TopicProgressRepository;
import Project.OpenBook.Utils.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static Project.OpenBook.Constants.ContentConst.*;
import static Project.OpenBook.Constants.ContentConst.CHAPTER_COMPLETE_QUESTION;
import static Project.OpenBook.Constants.StateConst.LOCKED;
import static Project.OpenBook.Constants.StateConst.OPEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChapterWithProgressService Class")
public class ChapterWithProgressServiceTest {

    @InjectMocks
    private ChapterWithProgressService chapterWithProgressService;

    @Mock
    private ChapterRepository chapterRepository;
    @Mock
    private ChapterSectionRepository chapterSectionRepository;

    @Mock private TopicProgressRepository topicProgressRepository;

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
                List<ChapterUserDto> chapterUserDtoList = chapterWithProgressService.queryChapterUserDtos(mockCustomer);

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
                List<ChapterUserDto> chapterUserDtoList = chapterWithProgressService.queryChapterUserDtos(mockCustomer);

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
                    List<ProgressDto> contentTableList = chapterWithProgressService.queryContentTable(mockCustomer, 1);

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
                    List<ProgressDto> contentTableList = chapterWithProgressService.queryContentTable(mockCustomer, 1);

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
                    List<ProgressDto> contentTableList = chapterWithProgressService.queryContentTable(mockCustomer, 1);

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
                    List<ProgressDto> contentTableList = chapterWithProgressService.queryContentTable(mockCustomer, 2);

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
                    chapterWithProgressService.queryContentTable(mockCustomer, num);
                });

                //then
                assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.CHAPTER_NOT_FOUND);
            }
        }
    }
}
