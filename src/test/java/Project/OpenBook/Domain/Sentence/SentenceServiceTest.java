package Project.OpenBook.Domain.Sentence;

import Project.OpenBook.Constants.ErrorCode;
import Project.OpenBook.Domain.Chapter.Domain.Chapter;
import Project.OpenBook.Domain.Sentence.Domain.Sentence;
import Project.OpenBook.Domain.Sentence.Dto.SentenceCreateDto;
import Project.OpenBook.Domain.Sentence.Dto.SentenceUpdateDto;
import Project.OpenBook.Domain.Sentence.Repository.SentenceRepository;
import Project.OpenBook.Domain.Sentence.Service.SentenceService;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import Project.OpenBook.Domain.Topic.Repo.TopicRepository;
import Project.OpenBook.Handler.Exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static Project.OpenBook.Constants.ErrorCode.SENTENCE_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("SentenceService Class")
public class SentenceServiceTest {

    @InjectMocks
    SentenceService sentenceService;

    @Mock
    SentenceRepository sentenceRepository;

    @Mock
    TopicRepository topicRepository;

    @Nested
    @DisplayName("createSentenceService() 메서드는")
    public class createSentenceServiceTest{

        private final String topicTitle = "topic1";
        private final String content = "sentence1";

        @Nested
        @DisplayName("문장과 토픽제목을 입력받으면")
        public class inputContentTopicTitle{

            @Test
            @DisplayName("해당 토픽에 문장을 생성하고 생성한 문장을 리턴한다. (중복 체크 x)")
            public void returnCreatedSentence(){
                //given
                Topic topic = new Topic(topicTitle, new Chapter("ch1", 1));
                given(topicRepository.findTopicByTitle(topicTitle))
                        .willReturn(Optional.ofNullable(topic));

                //when
                Sentence sentence = sentenceService.createSentence(new SentenceCreateDto(content, topicTitle));

                //then
                assertThat(sentence).usingRecursiveComparison().isEqualTo(new Sentence(content, topic));
            }

            @Nested
            @DisplayName("입력한 토픽 제목을 가진 토픽이 존재하지 않는 경우")
            public class notExistTopic{

                @Test
                @DisplayName("TOPIC_NOT_FOUND Exception을 날림")
                public void throwTopicNotFoundException(){
                    //given
                    given(topicRepository.findTopicByTitle(topicTitle))
                            .willReturn(Optional.empty());

                    //when
                    CustomException customException = assertThrows(CustomException.class, () -> {
                        sentenceService.createSentence(new SentenceCreateDto(content, topicTitle));
                    });

                    //then
                    assertThat(customException).usingRecursiveComparison().isEqualTo(new CustomException(ErrorCode.TOPIC_NOT_FOUND));
                }
            }
        }
    }

    @Nested
    @DisplayName("updateSentenceService() 메서드는")
    public class updateSentenceServiceTest{

        private Sentence prevSentence = new Sentence("s1", new Topic());
        private final Long sentenceId = 1L;
        private final String newContent = "new content!";

        @Nested
        @DisplayName("변경하려는 문장의 id와 변경하려는 내용을 입력받으면")
        public class inputSentenceIdName{

            @Test
            @DisplayName("해당 문장의 내용을 변경하고 변경한 문장을 리턴한다.")
            public void returnUpdatedSentence(){
                //given
                given(sentenceRepository.findById(sentenceId))
                        .willReturn(Optional.ofNullable(prevSentence));

                //when
                Sentence sentence = sentenceService.updateSentence(sentenceId, new SentenceUpdateDto(newContent));

                //then
                assertThat(sentence).usingRecursiveComparison().isEqualTo(new Sentence(newContent, prevSentence.getTopic()));

            }

            @Nested
            @DisplayName("입력받은 id를 가진 문장이 존재하지 않는 경우")
            public class notExistSentence {

                @Test
                @DisplayName("SENTENCE_NOT_FOUND Exception을 날린다.")
                public void throwSentenceNotFoundException() {
                    //given
                    given(sentenceRepository.findById(sentenceId))
                            .willReturn(Optional.empty());

                    //when
                    CustomException customException = assertThrows(CustomException.class, () -> {
                        sentenceService.updateSentence(sentenceId, new SentenceUpdateDto(newContent));
                    });

                    //then
                    assertThat(customException).usingRecursiveComparison()
                            .isEqualTo(new CustomException(SENTENCE_NOT_FOUND));
                }
            }

        }
    }

    @Nested
    @DisplayName("deleteSentenceService() 메서드는")
    public class deleteSentenceServiceTest{

        private final Long sentenceId = 1L;
        @Nested
        @DisplayName("삭제할 문장의 id를 입력받으면")
        public class inputSentenceId{

            @Test
            @DisplayName("해당 문장을 삭제하고 true를 리턴한다.")
            public void returnTrue() {
                //given
                given(sentenceRepository.findById(sentenceId))
                        .willReturn(Optional.ofNullable(new Sentence("prev", new Topic())));

                //when
                boolean ret = sentenceService.deleteSentence(sentenceId);

                //then
                assertThat(ret).isTrue();
            }

            @Nested
            @DisplayName("해당 id를 가진 문장이 존재하지 않는 경우")
            public class notExistSentence {
                @Test
                @DisplayName("SENTENCE_NOT_FOUND Exception을 날린다.")
                public void throwSentenceNotFoundException() {
                    //given
                    given(sentenceRepository.findById(sentenceId))
                            .willReturn(Optional.empty());

                    //when
                    CustomException customException = assertThrows(CustomException.class, () -> {
                        sentenceService.deleteSentence(sentenceId);
                    });

                    //then
                    assertThat(customException).usingRecursiveComparison().isEqualTo(new CustomException(SENTENCE_NOT_FOUND));

                }
            }

        }
    }
}
