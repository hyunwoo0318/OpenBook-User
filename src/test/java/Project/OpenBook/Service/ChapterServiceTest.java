package Project.OpenBook.Service;

import Project.OpenBook.Chapter.Domain.Chapter;
import Project.OpenBook.Chapter.Service.ChapterService;
import Project.OpenBook.Constants.ErrorCode;
import Project.OpenBook.Domain.*;
import Project.OpenBook.Chapter.Controller.dto.ChapterAddUpdateDto;
import Project.OpenBook.Chapter.Controller.dto.ChapterInfoDto;
import Project.OpenBook.Chapter.Repo.ChapterRepository;
import Project.OpenBook.Repository.chaptersection.ChapterSectionRepository;
import Project.OpenBook.Repository.topic.TopicRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
                Chapter chapter = new Chapter(chapterNum, chapterTitle, 10, 100);
                given(chapterRepository.save(any()))
                        .willReturn(chapter);

                //when
                Chapter findChapter = chapterService.createChapter(new ChapterAddUpdateDto(chapterTitle, chapterNum, 10, 100));

                //then
                assertThat(findChapter).isEqualTo(chapter);
            }

            @Test
            @DisplayName("이미 존재하는 단원 번호를 입력하면 DUP_CHAPTER_NUM Exception을 날린다.")
            public void dupChapterNum() {
                //given
                Chapter chapter = new Chapter(chapterNum,chapterTitle,  10, 100);
                given(chapterRepository.findOneByNumber(chapterNum))
                        .willReturn(Optional.of(chapter));

                //when
                CustomException customException = assertThrows(CustomException.class, () ->
                        chapterService.createChapter(new ChapterAddUpdateDto(chapterTitle, chapterNum, 10, 100)));
                //then
                assertEquals(customException.getErrorCode(), ErrorCode.DUP_CHAPTER_NUM);
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
                Chapter newChapter = new Chapter(newChapterNum, newChapterTitle, 10, 100);

                given(chapterRepository.findOneByNumber(prevChapterNum))
                        .willReturn(Optional.ofNullable(prevChapter));
                //변경할 단원번호를 가진 단원이 없는 경우
                given(chapterRepository.findOneByNumber(newChapterNum))
                        .willReturn(Optional.ofNullable(null));

                //when
                Chapter updatedChapter = chapterService.updateChapter(1, new ChapterAddUpdateDto(newChapterTitle, newChapterNum,
                        10, 100));

                //then
                assertThat(updatedChapter).isEqualTo(newChapter);
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
                    Chapter chapter = new Chapter("title2", newChapterNum);

                    given(chapterRepository.findOneByNumber(2))
                            .willReturn(Optional.ofNullable(chapter));

                    //when
                    CustomException customException = assertThrows(CustomException.class, () ->
                            chapterService.updateChapter(prevChapterNum, new ChapterAddUpdateDto(newChapterTitle, newChapterNum, 10, 100)));

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


}
