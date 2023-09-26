package Project.OpenBook.Domain.ExamQuestion;

import Project.OpenBook.Constants.ChoiceType;
import Project.OpenBook.Domain.Choice.Domain.Choice;
import Project.OpenBook.Domain.Choice.Repository.ChoiceRepository;
import Project.OpenBook.Domain.Description.Domain.Description;
import Project.OpenBook.Domain.ExamQuestion.Domain.ExamQuestion;
import Project.OpenBook.Domain.ExamQuestion.Repo.ExamQuestionRepository;
import Project.OpenBook.Domain.ExamQuestion.Service.ExamQuestionService;
import Project.OpenBook.Domain.ExamQuestion.Service.dto.ChoiceAddUpdateDto;
import Project.OpenBook.Domain.ExamQuestion.Service.dto.ExamQuestionInfoDto;
import Project.OpenBook.Domain.Round.Domain.Round;
import Project.OpenBook.Domain.Round.Repo.RoundRepository;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import Project.OpenBook.Domain.Topic.Repo.TopicRepository;
import Project.OpenBook.Handler.Exception.CustomException;
import Project.OpenBook.Image.ImageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import static Project.OpenBook.Constants.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChpaterService Class")
public class ExamQuestionServiceTest {
    @InjectMocks
    ExamQuestionService examQuestionService;
    @Mock
    ExamQuestionRepository examQuestionRepository;
    @Mock
    TopicRepository topicRepository;
    @Mock
    DescriptionRepository descriptionRepository;
    @Mock
    RoundRepository roundRepository;
    @Mock
    ChoiceRepository choiceRepository;
    @Mock
    ImageService imageService;


    @Nested
    @DisplayName("saveExamQuestionInfo() 메서드는")
    public class saveExamQuestionInfoTest{
        @Nested
        @DisplayName("round number와 ExamQuestionInfoDto를 입력하면")
        public class inputRoundNumberAndExamQuestionInfoDto {

            private final Integer roundNumber = 1;
            private final Round round = new Round(123, roundNumber);
            private final Integer questionNumber =1;
            private final String description = "encoded file";

            private final String descriptionUrl = "description stored url in Amazon s3";
            private final String descriptionComment = "description comment!";
            private final String answer = "topic1";
            private final Topic topic = new Topic(answer);
            private final String choiceType = ChoiceType.String.name();
            private final Integer score = 3;
            private final ExamQuestionInfoDto dto = new ExamQuestionInfoDto(questionNumber, description, descriptionComment, answer, choiceType, score);

            @Test
            @DisplayName("입력받은 정보로 ExamQuestion을 만들고 저장후 리턴한다.")
            public void returnSavedExamQuestion() throws IOException {
                //given
                given(roundRepository.findRoundByNumber(roundNumber))
                        .willReturn(Optional.ofNullable(round));

                given(examQuestionRepository.queryExamQuestion(roundNumber, questionNumber))
                        .willReturn(Optional.empty());

                given(topicRepository.findTopicByTitle(answer))
                        .willReturn(Optional.ofNullable(topic));

                doNothing().when(imageService).checkBase64(description);
                given(imageService.storeFile(description)).willReturn(descriptionUrl);

                //when
                ExamQuestion examQuestion = examQuestionService.saveExamQuestionInfo(roundNumber, dto);

                //then
                ExamQuestion expectExamQuestion = new ExamQuestion(round, questionNumber, score, ChoiceType.String);
                assertThat(examQuestion).usingRecursiveComparison().isEqualTo(expectExamQuestion);

            }

            @Nested
            @DisplayName("입력받은 roundNumber를 가진 round가 존재하지 않을경우")
            public class notExistRound {

                @Test
                @DisplayName("ROUND_NOT_FOUND Exeption을 날린다.")
                public void throwRoundNotFoundException(){
                    //given
                    given(roundRepository.findRoundByNumber(roundNumber))
                            .willReturn(Optional.empty());

                    //when
                    CustomException customException = assertThrows(CustomException.class, () -> {
                        examQuestionService.saveExamQuestionInfo(roundNumber, dto);
                    });

                    //then
                    assertThat(customException).usingRecursiveComparison()
                            .isEqualTo(new CustomException(ROUND_NOT_FOUND));

                }
            }

            @Nested
            @DisplayName("입력받은 ChoiceType이 유효하지 않은경우")
            public class notValidateChoiceType{

                @Test
                @DisplayName("NOT_VALIDATE_CHOICE_TYPE Exception을 날린다.")
                public void throwNotValidateChoiceTypeException(){
                    //given
                    given(roundRepository.findRoundByNumber(roundNumber))
                            .willReturn(Optional.ofNullable(round));
                    ExamQuestionInfoDto wrongDto =
                            new ExamQuestionInfoDto(questionNumber, description, descriptionComment, answer, "wrong choice type", score);

                    //when
                    CustomException customException = assertThrows(CustomException.class, () -> {
                        examQuestionService.saveExamQuestionInfo(roundNumber, wrongDto);
                    });

                    //then
                    assertThat(customException).usingRecursiveComparison()
                            .isEqualTo(new CustomException(NOT_VALIDATE_CHOICE_TYPE));

                }
            }

            @Nested
            @DisplayName("해당 round에 해당 번호를 가진 문제가 이미 존재하는 경우")
            public class existExamQuestion{

                @Test
                @DisplayName("DUP_QUESTION_NUMBER Exception을 날림")
                public void throwDupQuestionNumberException(){
                    //given
                    given(roundRepository.findRoundByNumber(roundNumber))
                            .willReturn(Optional.ofNullable(round));


                    given(examQuestionRepository.queryExamQuestion(roundNumber, questionNumber))
                            .willReturn(Optional.ofNullable(mock(ExamQuestion.class)));

                    //when
                    CustomException customException = assertThrows(CustomException.class, () -> {
                        examQuestionService.saveExamQuestionInfo(roundNumber, dto);
                    });

                    //then
                    assertThat(customException).usingRecursiveComparison()
                            .isEqualTo(new CustomException(DUP_QUESTION_NUMBER));
                }
            }

            @Nested
            @DisplayName("입력받은 topicTitle을 가진 topic이 존재하지 않는 경우")
            public class notExistTopic{

                @Test
                @DisplayName("TOPIC_NOT_FOUND Exception을 날린다.")
                public void throwTopicNotFoundException(){
                    //given
                    given(roundRepository.findRoundByNumber(roundNumber))
                            .willReturn(Optional.ofNullable(round));

                    given(examQuestionRepository.queryExamQuestion(roundNumber, questionNumber))
                            .willReturn(Optional.empty());

                    given(topicRepository.findTopicByTitle(answer))
                            .willReturn(Optional.empty());

                    //when
                    CustomException customException = assertThrows(CustomException.class, () -> {
                        examQuestionService.saveExamQuestionInfo(roundNumber, dto);
                    });

                    //then
                    assertThat(customException).usingRecursiveComparison()
                            .isEqualTo(new CustomException(TOPIC_NOT_FOUND));
                }
            }

            @Nested
            @DisplayName("입력받은 description이 base64 인코딩 되어있지 않는 경우")
            public class notEncodedDescription {

                @Test
                @DisplayName("NOT_VALIDATE_IAMGE Exception을 날림")
                public void throwNotValidateImageException(){
                    //given
                    given(roundRepository.findRoundByNumber(roundNumber))
                            .willReturn(Optional.ofNullable(round));

                    given(examQuestionRepository.queryExamQuestion(roundNumber, questionNumber))
                            .willReturn(Optional.empty());

                    given(topicRepository.findTopicByTitle(answer))
                            .willReturn(Optional.ofNullable(topic));

                    doThrow(new CustomException(NOT_VALIDATE_IMAGE)).when(imageService).checkBase64(description);

                    //when
                    CustomException customException = assertThrows(CustomException.class, () -> {
                        examQuestionService.saveExamQuestionInfo(roundNumber, dto);
                    });

                    //then
                    assertThat(customException).usingRecursiveComparison()
                            .isEqualTo(new CustomException(NOT_VALIDATE_IMAGE));
                }
            }

            @Nested
            @DisplayName("description을 Amazon S3에 저장을 실패한경우")
            public class imageSaveFailInS3{

                @Test
                @DisplayName("IMAGE_SAVE_FAIL Exception을 날린다.")
                public void throwImageSaveFailException(){
                    //given
                    given(roundRepository.findRoundByNumber(roundNumber))
                            .willReturn(Optional.ofNullable(round));

                    given(examQuestionRepository.queryExamQuestion(roundNumber, questionNumber))
                            .willReturn(Optional.empty());

                    given(topicRepository.findTopicByTitle(answer))
                            .willReturn(Optional.ofNullable(topic));

                    doNothing().when(imageService).checkBase64(description);
                    doThrow(new CustomException(IMAGE_SAVE_FAIL)).when(imageService).storeFile(description);

                    //when
                    CustomException customException = assertThrows(CustomException.class, () -> {
                        examQuestionService.saveExamQuestionInfo(roundNumber, dto);
                    });

                    //then
                    assertThat(customException).usingRecursiveComparison()
                            .isEqualTo(new CustomException(IMAGE_SAVE_FAIL));
                }
            }




        }
    }

    @Nested
    @DisplayName("saveExamQuestionChoice() 메서드는")
    public class saveExamQuestionChoiceTest{

        private final Integer roundNumber = 1;
        private final Round round = new Round(123, roundNumber);
        private final Integer questionNumber =1;
        private final String choice = "encoded file";

        private final String choiceUrl = "choice stored url in Amazon s3";
        private final String choiceComment = "choice comment!";
        private final String answer = "topic1";
        private final Topic topic = new Topic(answer);

        private final ChoiceAddUpdateDto stringChoiceDto = new ChoiceAddUpdateDto(choice, choiceComment, answer, ChoiceType.String.name());
        private final ChoiceAddUpdateDto imageChoiceDto = new ChoiceAddUpdateDto(choice, choiceComment, answer, ChoiceType.Image.name());
        @Nested
        @DisplayName("roundNumber, questionNumber, choiceAddUpdateDto를 입력받으면")
        public class inputRoundNumQuestionNumChoiceAddUpdateDto {

            @Nested
            @DisplayName("choiceType이 String인경우")
            public class inputStringChoice{

                @Test
                @DisplayName("해당 선지를 저장하고 리턴한다.")
                public void returnSavedChoice() throws IOException {
                    //given
                    ExamQuestion mockExamQuestion = mock(ExamQuestion.class);
                    given(roundRepository.findRoundByNumber(roundNumber))
                            .willReturn(Optional.ofNullable(round));
                    given(examQuestionRepository.queryExamQuestion(roundNumber, questionNumber))
                            .willReturn(Optional.ofNullable(mockExamQuestion));
                    given(topicRepository.findTopicByTitle(answer))
                            .willReturn(Optional.ofNullable(topic));

                    //when
                    Choice savedChoice = examQuestionService.saveExamQuestionChoice(roundNumber, questionNumber, stringChoiceDto);

                    //then
                    assertThat(savedChoice).usingRecursiveComparison()
                            .isEqualTo(new Choice(ChoiceType.String, choice, choiceComment, topic, mockExamQuestion));
                }

            }

            @Nested
            @DisplayName("choiceType이 Image인경우")
            public class inputImageChoice{

                @Test
                @DisplayName("해당 선지를 저장하고 리턴한다.")
                public void returnSavedChoice() throws IOException {
                    //given
                    ExamQuestion mockExamQuestion = mock(ExamQuestion.class);
                    given(roundRepository.findRoundByNumber(roundNumber))
                            .willReturn(Optional.ofNullable(round));
                    given(examQuestionRepository.queryExamQuestion(roundNumber, questionNumber))
                            .willReturn(Optional.ofNullable(mockExamQuestion));
                    given(topicRepository.findTopicByTitle(answer))
                            .willReturn(Optional.ofNullable(topic));

                    doNothing().when(imageService).checkBase64(choice);
                    given(imageService.storeFile(choice))
                            .willReturn(choiceUrl);

                    //when
                    Choice savedChoice = examQuestionService.saveExamQuestionChoice(roundNumber, questionNumber, imageChoiceDto);

                    //then
                    assertThat(savedChoice).usingRecursiveComparison()
                            .isEqualTo(new Choice(ChoiceType.Image, choiceUrl, choiceComment, topic, mockExamQuestion));
                }

                @Nested
                @DisplayName("선지가 정상적으로 base64 인코딩 되어있지 않은 경우")
                public class notEncodedChoice{

                    @Test
                    @DisplayName("NOT_VALIDATE_IMAGE Exception을 던진다.")
                    public void throwNotValidateImageException(){
                        //given
                        ExamQuestion mockExamQuestion = mock(ExamQuestion.class);
                        given(roundRepository.findRoundByNumber(roundNumber))
                                .willReturn(Optional.ofNullable(round));
                        given(examQuestionRepository.queryExamQuestion(roundNumber, questionNumber))
                                .willReturn(Optional.ofNullable(mockExamQuestion));
                        given(topicRepository.findTopicByTitle(answer))
                                .willReturn(Optional.ofNullable(topic));

                        doThrow(new CustomException(NOT_VALIDATE_IMAGE)).when(imageService).checkBase64(choice);

                        //when
                        CustomException customException = assertThrows(CustomException.class, () -> {
                            examQuestionService.saveExamQuestionChoice(roundNumber, questionNumber, imageChoiceDto);
                        });

                        //then
                        assertThat(customException).usingRecursiveComparison()
                                .isEqualTo(new CustomException(NOT_VALIDATE_IMAGE));
                    }

                }

                @Nested
                @DisplayName("선지 이미지를 Amazon S3에 저장을 실패한경우")
                public class imageSaveFailInS3{

                    @Test
                    @DisplayName("IMAGE_SAVE_FAIL Exception을 날린다.")
                    public void throwImageSaveFailException(){
                        //given
                        ExamQuestion mockExamQuestion = mock(ExamQuestion.class);
                        given(roundRepository.findRoundByNumber(roundNumber))
                                .willReturn(Optional.ofNullable(round));
                        given(examQuestionRepository.queryExamQuestion(roundNumber, questionNumber))
                                .willReturn(Optional.ofNullable(mockExamQuestion));
                        given(topicRepository.findTopicByTitle(answer))
                                .willReturn(Optional.ofNullable(topic));

                        doNothing().when(imageService).checkBase64(choice);
                        doThrow(new CustomException(IMAGE_SAVE_FAIL)).when(imageService).storeFile(choice);

                        //when
                        CustomException customException = assertThrows(CustomException.class, () -> {
                            examQuestionService.saveExamQuestionChoice(roundNumber, questionNumber, imageChoiceDto);
                        });

                        //then
                        assertThat(customException).usingRecursiveComparison()
                                .isEqualTo(new CustomException(IMAGE_SAVE_FAIL));

                    }


            }


        }

            @Nested
            @DisplayName("입력받은 roundNumber를 가진 round가 존재하지 않는 경우")
            public class notExistRound{

                @Test
                @DisplayName("ROUND_NOT_FOUND Exception을 날린다.")
                public void throwRoundNotFoundException(){
                    //given
                    given(roundRepository.findRoundByNumber(roundNumber))
                            .willReturn(Optional.empty());

                    //when
                    CustomException customException = assertThrows(CustomException.class, () -> {
                        examQuestionService.saveExamQuestionChoice(roundNumber, questionNumber, stringChoiceDto);
                    });

                    //then
                    assertThat(customException).usingRecursiveComparison()
                            .isEqualTo(new CustomException(ROUND_NOT_FOUND));
                }
            }

            @Nested
            @DisplayName("해당 round에 해당 번호를 가진 문제가 존재하지 않는 경우")
            public class notExistExamQuestion{

                @Test
                @DisplayName("QUESTION_NOT_FOUND Exeption을 리턴한다.")
                public void throwQuestionNotFoundException(){
                    //given
                    given(roundRepository.findRoundByNumber(roundNumber))
                            .willReturn(Optional.ofNullable(round));
                    given(examQuestionRepository.queryExamQuestion(roundNumber, questionNumber))
                            .willReturn(Optional.empty());

                    //when
                    CustomException customException = assertThrows(CustomException.class, () -> {
                        examQuestionService.saveExamQuestionChoice(roundNumber, questionNumber, stringChoiceDto);
                    });

                    //then
                    assertThat(customException).usingRecursiveComparison()
                            .isEqualTo(new CustomException(QUESTION_NOT_FOUND));
                }

            }

            @Nested
            @DisplayName("입력받은 ChoiceType이 유효하지 않은경우")
            public class notValidateChoiceType{

                @Test
                @DisplayName("NOT_VALIDATE_CHOICE_TYPE Exception을 날린다.")
                public void throwNotValidateChoiceTypeException(){
                    //given
                    ExamQuestion mockExamQuestion = mock(ExamQuestion.class);
                    given(roundRepository.findRoundByNumber(roundNumber))
                            .willReturn(Optional.ofNullable(round));
                    given(examQuestionRepository.queryExamQuestion(roundNumber, questionNumber))
                            .willReturn(Optional.ofNullable(mockExamQuestion));

                    //when
                    CustomException customException = assertThrows(CustomException.class, () -> {
                        examQuestionService.saveExamQuestionChoice(roundNumber, questionNumber,
                                new ChoiceAddUpdateDto(choice, choiceComment, answer,"wrong choice type!!"));
                    });

                    //then
                    assertThat(customException).usingRecursiveComparison()
                            .isEqualTo(new CustomException(NOT_VALIDATE_CHOICE_TYPE));

                }
            }


            @Nested
            @DisplayName("입력받은 topicTitle을 가진 topic이 존재하지 않는 경우")
            public class notExistTopic{

                @Test
                @DisplayName("TOPIC_NOT_FOUND Exception을 날린다.")
                public void throwTopicNotFoundException(){
                    //given
                    ExamQuestion mockExamQuestion = mock(ExamQuestion.class);
                    given(roundRepository.findRoundByNumber(roundNumber))
                            .willReturn(Optional.ofNullable(round));
                    given(examQuestionRepository.queryExamQuestion(roundNumber, questionNumber))
                            .willReturn(Optional.ofNullable(mockExamQuestion));
                    given(topicRepository.findTopicByTitle(answer))
                            .willReturn(Optional.empty());


                    //when
                    CustomException customException = assertThrows(CustomException.class, () -> {
                        examQuestionService.saveExamQuestionChoice(roundNumber, questionNumber, stringChoiceDto);
                    });

                    //then
                    assertThat(customException).usingRecursiveComparison()
                            .isEqualTo(new CustomException(TOPIC_NOT_FOUND));
                }
            }
            }
    }

    @Nested
    @DisplayName("updateExamQuestion() 메서드는")
    public class updateExamQuestionTest{

        private final Integer roundNumber = 1;
        private final Round round = new Round(123, roundNumber);
        private final Integer prevQuestionNumber = 1;
        private final Integer questionNumber =2;
        private final String description = "encoded file";

        private final String descriptionUrl = "https://example-url";
        private final String descriptionComment = "description comment!";
        private final String answer = "topic1";
        private final Topic topic = new Topic(answer);
        private final String choiceType = ChoiceType.String.name();
        private final Integer score = 3;

        private final ExamQuestion prevExamQuestion = new ExamQuestion(round, prevQuestionNumber, 3, ChoiceType.String);

        private final ExamQuestionInfoDto urlDto = new ExamQuestionInfoDto(questionNumber, descriptionUrl, descriptionComment, answer, choiceType, score);
        private final ExamQuestionInfoDto encodedDto = new ExamQuestionInfoDto(questionNumber, description, descriptionComment, answer, choiceType, score);

        @Nested
        @DisplayName("round number, questionNumber, ExamQuestionInfoDto를 입력하면")
        public class inputRoundNumberQuestionNumberExamQuestionInfoDto{

            @Test
            @DisplayName("입력받은 정보로 문제를 update하고 리턴한다.")
            public void returnUpdatedExamQuestion() throws IOException {
                //given
                ExamQuestion mockExamQuestion = mock(ExamQuestion.class);
                given(roundRepository.findRoundByNumber(roundNumber))
                        .willReturn(Optional.ofNullable(round));
                given(examQuestionRepository.queryExamQuestionWithDescriptionAndTopic(roundNumber, prevQuestionNumber))
                        .willReturn(Optional.ofNullable(mockExamQuestion));
                given(examQuestionRepository.queryExamQuestion(roundNumber, questionNumber))
                        .willReturn(Optional.empty());
                given(mockExamQuestion.updateExamQuestion(questionNumber, score, ChoiceType.String))
                        .willReturn(new ExamQuestion(round, questionNumber, score, ChoiceType.String));
                doReturn(mock(Description.class)).when(mockExamQuestion).getDescription();

                //when
                ExamQuestion examQuestion = examQuestionService.updateExamQuestionInfo(roundNumber, prevQuestionNumber, encodedDto);

                //then
                assertThat(examQuestion.getNumber()).isEqualTo(encodedDto.getNumber());
                assertThat(examQuestion.getScore()).isEqualTo(encodedDto.getScore());
                assertThat(examQuestion.getChoiceType().name()).isEqualTo(encodedDto.getChoiceType());
            }

            @Nested
            @DisplayName("입력받은 description이 base64 인코딩 되어있지 않는 경우")
            public class notEncodedDescription {

                @Test
                @DisplayName("NOT_VALIDATE_IAMGE Exception을 날림")
                public void throwNotValidateImageException(){
                    //given
                    ExamQuestion mockExamQuestion = mock(ExamQuestion.class);
                    given(roundRepository.findRoundByNumber(roundNumber))
                            .willReturn(Optional.ofNullable(round));
                    given(examQuestionRepository.queryExamQuestionWithDescriptionAndTopic(roundNumber, prevQuestionNumber))
                            .willReturn(Optional.ofNullable(mockExamQuestion));
                    given(examQuestionRepository.queryExamQuestion(roundNumber, questionNumber))
                            .willReturn(Optional.empty());
                    given(mockExamQuestion.updateExamQuestion(questionNumber, score, ChoiceType.String))
                            .willReturn(new ExamQuestion(round, questionNumber, score, ChoiceType.String));
                    doReturn(mock(Description.class)).when(mockExamQuestion).getDescription();
                    doThrow(new CustomException(NOT_VALIDATE_IMAGE)).when(imageService).checkBase64(description);

                    //when
                    CustomException customException = assertThrows(CustomException.class, () -> {
                        examQuestionService.updateExamQuestionInfo(roundNumber, prevQuestionNumber,encodedDto);
                    });

                    //then
                    assertThat(customException).usingRecursiveComparison()
                            .isEqualTo(new CustomException(NOT_VALIDATE_IMAGE));
                }
            }

            @Nested
            @DisplayName("description을 Amazon S3에 저장을 실패한경우")
            public class imageSaveFailInS3{

                @Test
                @DisplayName("IMAGE_SAVE_FAIL Exception을 날린다.")
                public void throwImageSaveFailException(){
                    //given
                    ExamQuestion mockExamQuestion = mock(ExamQuestion.class);
                    given(roundRepository.findRoundByNumber(roundNumber))
                            .willReturn(Optional.ofNullable(round));
                    given(examQuestionRepository.queryExamQuestionWithDescriptionAndTopic(roundNumber, prevQuestionNumber))
                            .willReturn(Optional.ofNullable(mockExamQuestion));
                    given(examQuestionRepository.queryExamQuestion(roundNumber, questionNumber))
                            .willReturn(Optional.empty());
                    given(mockExamQuestion.updateExamQuestion(questionNumber, score, ChoiceType.String))
                            .willReturn(new ExamQuestion(round, questionNumber, score, ChoiceType.String));
                    doReturn(mock(Description.class)).when(mockExamQuestion).getDescription();
                    doNothing().when(imageService).checkBase64(description);
                    doThrow(new CustomException(IMAGE_SAVE_FAIL)).when(imageService).storeFile(description);

                    //when
                    CustomException customException = assertThrows(CustomException.class, () -> {
                        examQuestionService.updateExamQuestionInfo(roundNumber, prevQuestionNumber,encodedDto);
                    });

                    //then
                    assertThat(customException).usingRecursiveComparison()
                            .isEqualTo(new CustomException(IMAGE_SAVE_FAIL));
                }
            }

            @Nested
            @DisplayName("입력한 description이 s3주소인경우 (이미지의 변경이 없는 경우)")
            public class descriptionIsUrl{

                @Test
                @DisplayName("따로 저장하지 않는다.")
                public void notSaveDescription() throws IOException {
                    //given
                    ExamQuestion mockExamQuestion = mock(ExamQuestion.class);
                    given(roundRepository.findRoundByNumber(roundNumber))
                            .willReturn(Optional.ofNullable(round));
                    given(examQuestionRepository.queryExamQuestionWithDescriptionAndTopic(roundNumber, prevQuestionNumber))
                            .willReturn(Optional.ofNullable(mockExamQuestion));
                    given(examQuestionRepository.queryExamQuestion(roundNumber, questionNumber))
                            .willReturn(Optional.empty());
                    given(mockExamQuestion.updateExamQuestion(questionNumber, score, ChoiceType.String))
                            .willReturn(new ExamQuestion(round, questionNumber, score, ChoiceType.String));
                    doReturn(mock(Description.class)).when(mockExamQuestion).getDescription();

                    //when
                    ExamQuestion examQuestion = examQuestionService.updateExamQuestionInfo(roundNumber, prevQuestionNumber, urlDto);

                    //then
                    assertThat(examQuestion.getNumber()).isEqualTo(encodedDto.getNumber());
                    assertThat(examQuestion.getScore()).isEqualTo(encodedDto.getScore());
                    assertThat(examQuestion.getChoiceType().name()).isEqualTo(encodedDto.getChoiceType());
                }
            }
            @Nested
            @DisplayName("기존의 문제번호와 입력받은 문제번호가 다른 경우")
            public class notSameQuestionNumber{
                @Nested
                @DisplayName("입력받은 문제번호를 가진 문제가 존재하는 경우")
                public class existExamQuestion{
                    @Test
                    @DisplayName("DUP_QUESTION_NUMBER Exception을 날린다.")
                    public void throwDupQuestionNumberException(){
                        //given
                        ExamQuestion mockExamQuestion = mock(ExamQuestion.class);
                        given(roundRepository.findRoundByNumber(roundNumber))
                                .willReturn(Optional.ofNullable(round));
                        given(examQuestionRepository.queryExamQuestionWithDescriptionAndTopic(roundNumber, prevQuestionNumber))
                                .willReturn(Optional.ofNullable(mockExamQuestion));
                        given(examQuestionRepository.queryExamQuestion(roundNumber, questionNumber))
                                .willReturn(Optional.ofNullable(mock(ExamQuestion.class)));

                        //when
                        CustomException customException = assertThrows(CustomException.class, () -> {
                            examQuestionService.updateExamQuestionInfo(roundNumber, prevQuestionNumber, encodedDto);
                        });

                        //then
                        assertThat(customException).usingRecursiveComparison()
                                .isEqualTo(new CustomException(DUP_QUESTION_NUMBER));
                    }

                }
            }

            @Nested
            @DisplayName("입력받은 roundNumber를 가진 round가 존재하지 않는 경우")
            public class notExistRound{

                @Test
                @DisplayName("ROUND_NOT_FOUND Exception을 날린다.")
                public void throwRoundNotFoundException(){
                    //given
                    given(roundRepository.findRoundByNumber(roundNumber))
                            .willReturn(Optional.empty());

                    //when
                    CustomException customException = assertThrows(CustomException.class, () -> {
                        examQuestionService.updateExamQuestionInfo(roundNumber, prevQuestionNumber, encodedDto);
                    });

                    //then
                    assertThat(customException).usingRecursiveComparison()
                            .isEqualTo(new CustomException(ROUND_NOT_FOUND));

                }
            }

            @Nested
            @DisplayName("해당 round에 해당 번호를 가진 문제가 존재하지 않는 경우")
            public class notExistExamQuestion{

                @Test
                @DisplayName("QUESTION_NOT_FOUND Exeption을 리턴한다.")
                public void throwQuestionNotFoundException(){
                    //given
                    given(roundRepository.findRoundByNumber(roundNumber))
                            .willReturn(Optional.ofNullable(round));
                    given(examQuestionRepository.queryExamQuestionWithDescriptionAndTopic(roundNumber, prevQuestionNumber))
                            .willReturn(Optional.empty());

                    //when
                    CustomException customException = assertThrows(CustomException.class, () -> {
                        examQuestionService.updateExamQuestionInfo(roundNumber, prevQuestionNumber, encodedDto);
                    });

                    //then
                    assertThat(customException).usingRecursiveComparison()
                            .isEqualTo(new CustomException(QUESTION_NOT_FOUND));
                }

            }

            @Nested
            @DisplayName("입력받은 ChoiceType이 유효하지 않은경우")
            public class notValidateChoiceType{

                @Test
                @DisplayName("NOT_VALIDATE_CHOICE_TYPE Exception을 날린다.")
                public void throwNotValidateChoiceTypeException(){
                    //given
                    ExamQuestion mockExamQuestion = mock(ExamQuestion.class);
                    given(roundRepository.findRoundByNumber(roundNumber))
                            .willReturn(Optional.ofNullable(round));
                    given(examQuestionRepository.queryExamQuestionWithDescriptionAndTopic(roundNumber, prevQuestionNumber))
                            .willReturn(Optional.ofNullable(mockExamQuestion));

                    //when
                    CustomException customException = assertThrows(CustomException.class, () -> {
                        examQuestionService.updateExamQuestionInfo(roundNumber, prevQuestionNumber,
                                new ExamQuestionInfoDto(questionNumber, description, descriptionComment, answer, "wrong choice type", score));
                    });

                    //then
                    assertThat(customException).usingRecursiveComparison()
                            .isEqualTo(new CustomException(NOT_VALIDATE_CHOICE_TYPE));
                }
            }



        }
    }

    @Nested
    @DisplayName("deleteExamQuestion() 메서드는")
    public class deleteExamQuestionTest{

        private final Integer roundNumber = 1;
        private final Round round = new Round(123, roundNumber);
        private final Integer questionNumber = 1;

        @Test
        @DisplayName("해당 문제를 삭제하고 true를 리턴한다.")
        public void returnTrue(){
            //given
            ExamQuestion mockExamQuestion = mock(ExamQuestion.class);
            given(roundRepository.findRoundByNumber(roundNumber))
                    .willReturn(Optional.of(round));
            given(examQuestionRepository.queryExamQuestionWithDescriptionAndTopic(roundNumber, questionNumber))
                    .willReturn(Optional.of(mockExamQuestion));
            doReturn(mock(Description.class)).when(mockExamQuestion).getDescription();
            doReturn(new ArrayList<>()).when(mockExamQuestion).getChoiceList();

            //when
            Boolean ret = examQuestionService.deleteExamQuestion(roundNumber, questionNumber);

            //then
            assertThat(ret).isTrue();


        }


        @Nested
        @DisplayName("입력받은 roundNumber를 가진 round가 존재하지 않는 경우")
        public class notExistRound{

            @Test
            @DisplayName("ROUND_NOT_FOUND Exception을 날린다.")
            public void throwRoundNotFoundException(){
                //given
                given(roundRepository.findRoundByNumber(roundNumber))
                        .willReturn(Optional.empty());

                //when
                CustomException customException = assertThrows(CustomException.class, () -> {
                    examQuestionService.deleteExamQuestion(roundNumber, questionNumber);
                });

                //then
                assertThat(customException).usingRecursiveComparison()
                        .isEqualTo(new CustomException(ROUND_NOT_FOUND));

            }
        }

        @Nested
        @DisplayName("해당 round에 해당 번호를 가진 문제가 존재하지 않는 경우")
        public class notExistExamQuestion{

            @Test
            @DisplayName("QUESTION_NOT_FOUND Exeption을 리턴한다.")
            public void throwQuestionNotFoundException(){
                //given
                given(roundRepository.findRoundByNumber(roundNumber))
                        .willReturn(Optional.of(round));
                given(examQuestionRepository.queryExamQuestionWithDescriptionAndTopic(roundNumber, questionNumber))
                        .willReturn(Optional.empty());

                //when
                CustomException customException = assertThrows(CustomException.class, () -> {
                    examQuestionService.deleteExamQuestion(roundNumber, questionNumber);
                });

                //then
                assertThat(customException).usingRecursiveComparison()
                        .isEqualTo(new CustomException(QUESTION_NOT_FOUND));
            }

        }
    }

}
