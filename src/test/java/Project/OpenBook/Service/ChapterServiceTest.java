package Project.OpenBook.Service;

import Project.OpenBook.Constants.ErrorCode;
import Project.OpenBook.Domain.Chapter;
import Project.OpenBook.Dto.chapter.ChapterDto;
import Project.OpenBook.Dto.topic.ChapterAdminDto;
import Project.OpenBook.Repository.chapter.ChapterRepository;
import Project.OpenBook.Repository.topic.TopicRepository;
import Project.OpenBook.Utils.CustomException;
import com.querydsl.core.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static Project.OpenBook.Domain.QChoice.choice;
import static Project.OpenBook.Domain.QDescription.description;
import static Project.OpenBook.Domain.QKeyword.keyword;
import static Project.OpenBook.Domain.QTopic.topic;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChpaterService Class")
public class ChapterServiceTest {

    @InjectMocks
    private ChapterService chapterService;

    @Mock
    private ChapterRepository chapterRepository;

    @Mock
    private TopicRepository topicRepository;

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
        @DisplayName("단원 번호를 입력하면")
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
}
