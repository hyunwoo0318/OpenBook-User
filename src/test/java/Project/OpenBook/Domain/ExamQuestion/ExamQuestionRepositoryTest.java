package Project.OpenBook.Domain.ExamQuestion;

import Project.OpenBook.Config.TestQueryDslConfig;
import Project.OpenBook.Constants.ChoiceType;
import Project.OpenBook.Domain.Choice.Repository.ChoiceRepository;
import Project.OpenBook.Domain.Description.Domain.Description;
import Project.OpenBook.Domain.Description.Repository.DescriptionRepository;
import Project.OpenBook.Domain.ExamQuestion.Domain.ExamQuestion;
import Project.OpenBook.Domain.ExamQuestion.Repo.ExamQuestionRepository;
import Project.OpenBook.Domain.Round.Domain.Round;
import Project.OpenBook.Domain.Round.Repo.RoundRepository;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import Project.OpenBook.Domain.Topic.Repo.TopicRepository;
import org.junit.jupiter.api.*;
import org.mockito.configuration.IMockitoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestQueryDslConfig.class)
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("ExamQuestionRepository class")
@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
public class ExamQuestionRepositoryTest {

    @Autowired
    ExamQuestionRepository examQuestionRepository;

    @Autowired
    RoundRepository roundRepository;

    @Autowired
    TopicRepository topicRepository;

    @Autowired
    ChoiceRepository choiceRepository;

    @Autowired
    DescriptionRepository descriptionRepository;

    @Nested
    @DisplayName("queryExamQuestion() 메서드는")
    public class queryExamQuestionTest{

        private final Integer roundNumber = 1;
        private final Integer questionNumber = 2;

        @Nested
        @DisplayName("roundNumber과 questionNumber를 입력받으면")
        public class inputRoundNumberQuestionNumber{

            @AfterEach
            public void clear(){
                examQuestionRepository.deleteAllInBatch();
                roundRepository.deleteAllInBatch();
            }

            @Test
            @DisplayName("해당 조건에 맞는 ExamQuestion을 Optional로 감싸서 리턴한다.")
            public void returnExamQuestionOptional() {
                //given
                Round round = new Round(123, roundNumber);
                roundRepository.save(round);
                ExamQuestion examQuestion = new ExamQuestion(round, questionNumber, 3, ChoiceType.String);
                examQuestionRepository.save(examQuestion);

                //when
                Optional<ExamQuestion> examQuestionOptional =
                        examQuestionRepository.queryExamQuestion(roundNumber, questionNumber);

                //then
                assertThat(examQuestionOptional.get()).usingRecursiveComparison()
                        .isEqualTo(examQuestion);


            }

            @Test
            @DisplayName("해당 조건에 맞는 문제가 없으면 Optional.empty()를 리턴한다.")
            public void returnOptionalEmpty(){
                //given
                Round round1 = new Round(123, roundNumber);
                Round round2 = new Round(123, 123);
                roundRepository.saveAll(Arrays.asList(round1, round2));
                ExamQuestion examQuestion1 = new ExamQuestion(round1, 3, 3, ChoiceType.String);
                ExamQuestion examQuestion2 = new ExamQuestion(round2, questionNumber, 3, ChoiceType.String);
                examQuestionRepository.saveAll(Arrays.asList(examQuestion1, examQuestion2));

                //when
                Optional<ExamQuestion> examQuestionOptional =
                        examQuestionRepository.queryExamQuestion(roundNumber, questionNumber);

                //then
                assertThat(examQuestionOptional.isEmpty()).isTrue();
            }
        }
    }

    @Nested
    @DisplayName("queryExamQuestionWithDescriptionAndTopic() 메서드는")
    public class queryExamQuestionWithDescriptionAndTopicTest{

        private final Integer roundNumber = 1;
        private final Integer questionNumber = 2;

        @Nested
        @DisplayName("roundNumber과 questionNumber를 입력하면")
        public class inputRoundNumberQuestionNumber {

            @Test
            @DisplayName("해당 조건에 맞는 examQuestion을 topic과 description을 fetch join해서 Optional로 감싸서 리턴한다.")
            public void returnExamQuestion_FetchJoinTopicDescription_Optional(){
                //given
                Topic topic = new Topic("topic1");
                topicRepository.save(topic);

                Round round = new Round(123, roundNumber);
                roundRepository.save(round);
                ExamQuestion examQuestion = new ExamQuestion(round, questionNumber, 3, ChoiceType.String);
                examQuestionRepository.save(examQuestion);

                Description description = new Description("des1", "comment1", topic, examQuestion);
                descriptionRepository.save(description);

                //when
                Optional<ExamQuestion> examQuestionOptional
                        = examQuestionRepository.queryExamQuestionWithDescriptionAndTopic(roundNumber, questionNumber);

                //then
                ExamQuestion findExamQuestion = examQuestionOptional.get();
                assertThat(findExamQuestion).usingRecursiveComparison()
                        .isEqualTo(examQuestion);
                assertThat(findExamQuestion.getDescription()).usingRecursiveComparison()
                        .isEqualTo(description);
                assertThat(findExamQuestion.getDescription().getTopic()).usingRecursiveComparison()
                        .isEqualTo(topic);

            }
        }
    }

}
