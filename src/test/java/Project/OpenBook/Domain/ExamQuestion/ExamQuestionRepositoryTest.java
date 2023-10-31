package Project.OpenBook.Domain.ExamQuestion;

import Project.OpenBook.Config.TestQueryDslConfig;
import Project.OpenBook.Constants.ChoiceType;
import Project.OpenBook.Domain.Description.Domain.Description;
import Project.OpenBook.Domain.ExamQuestion.Domain.ExamQuestion;
import Project.OpenBook.Domain.ExamQuestion.Repo.ExamQuestionRepository;
import Project.OpenBook.Domain.Round.Domain.Round;
import Project.OpenBook.Domain.Round.Repo.RoundRepository;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import Project.OpenBook.Domain.Topic.Repo.TopicRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;
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
    DescriptionRepository descriptionRepository;

    @Autowired
    EntityManager entityManager;

    private void baseCLear(){
        descriptionRepository.deleteAllInBatch();
        examQuestionRepository.deleteAllInBatch();
        roundRepository.deleteAllInBatch();
        topicRepository.deleteAllInBatch();
    }

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

                entityManager.clear();

                //when
                Optional<ExamQuestion> examQuestionOptional
                        = examQuestionRepository.queryExamQuestionWithDescriptionAndTopic(roundNumber, questionNumber);

                //then
                ExamQuestion findExamQuestion = examQuestionOptional.get();
                assertThat(findExamQuestion.getId()).isEqualTo(examQuestion.getId());
                
                //fetch join 확인
                assertThat(findExamQuestion.getDescription().getId()).isEqualTo(description.getId());
                assertThat(findExamQuestion.getDescription().getTopic().getId()).isEqualTo(topic.getId());

                baseCLear();
            }

            @Test
            @DisplayName("examQuestion에 topic과 description이 없어도 리턴한다.")
            public void returnExamQuestion_whenNotExistTopicDescription() {
                //given
                Round round = new Round(123, roundNumber);
                roundRepository.save(round);
                ExamQuestion examQuestion = new ExamQuestion(round, questionNumber, 3, ChoiceType.String);
                examQuestionRepository.save(examQuestion);

                //when
                Optional<ExamQuestion> examQuestionOptional
                        = examQuestionRepository.queryExamQuestionWithDescriptionAndTopic(roundNumber, questionNumber);

                //then
                ExamQuestion findExamQuestion = examQuestionOptional.get();
                assertThat(findExamQuestion.getId()).isEqualTo(examQuestion.getId());

                baseCLear();
            }

        }
    }

    @Nested
    @DisplayName("queryExamQuestionsWithDescriptionAndTopic() 메서드는")
    public class queryExamQuestionsWithDescriptionAndTopicTest{

        private final Integer roundNumber = 1;

        @Nested
        @DisplayName("roundNumber를 입력하면")
        public class inputRoundNumber{

            @Test
            @DisplayName("해당 round내의 모든 문제들을 description,topic을 fetch join하고 문제 순서대로 정렬해서 리턴한다.")
            public void returnExamQuestions_fetchJoinDescriptionTopic_sortByQuestionNumber(){
                //given
                Topic topic1 = new Topic("topic1");
                Topic topic2 = new Topic("topic2");
                topicRepository.saveAll(Arrays.asList(topic1, topic2));

                Round round = new Round(123, roundNumber);
                roundRepository.save(round);
                ExamQuestion examQuestion1 = new ExamQuestion(round, 1, 3, ChoiceType.String);
                ExamQuestion examQuestion2 = new ExamQuestion(round, 2, 3, ChoiceType.String);
                examQuestionRepository.saveAll(Arrays.asList(examQuestion1, examQuestion2));

                Description description1 = new Description("des1", "comment1", topic1, examQuestion1);
                Description description2 = new Description("des2", "comment2", topic2, examQuestion2);
                descriptionRepository.saveAll(Arrays.asList(description1, description2));

                entityManager.clear();

                //when
                List<ExamQuestion> examQuestionList
                        = examQuestionRepository.queryExamQuestionsForExamQuestionList(roundNumber);

                //then
                assertThat(examQuestionList.size()).isEqualTo(2);

                ExamQuestion ret1 = examQuestionList.get(0);
                ExamQuestion ret2 = examQuestionList.get(1);
                //examQuestion1 체크
                assertThat(ret1.getId()).isEqualTo(examQuestion1.getId());
                assertThat(ret1.getDescription().getId()).isEqualTo(description1.getId());
                assertThat(ret1.getDescription().getTopic().getId()).isEqualTo(topic1.getId());

                //examQuestion1 체크
                assertThat(ret2.getId()).isEqualTo(examQuestion2.getId());
                assertThat(ret2.getDescription().getId()).isEqualTo(description2.getId());
                assertThat(ret2.getDescription().getTopic().getId()).isEqualTo(topic2.getId());

                baseCLear();

            }

            @Test
            @DisplayName("문제에 description이 없어도 리턴한다.")
            public void returnExamQuestions_whenNotExistDescription(){
                //given
                Round round = new Round(123, roundNumber);
                roundRepository.save(round);
                ExamQuestion examQuestion1 = new ExamQuestion(round, 1, 3, ChoiceType.String);
                ExamQuestion examQuestion2 = new ExamQuestion(round, 2, 3, ChoiceType.String);
                examQuestionRepository.saveAll(Arrays.asList(examQuestion1, examQuestion2));

                entityManager.clear();

                //when
                List<ExamQuestion> examQuestionList
                        = examQuestionRepository.queryExamQuestionsForExamQuestionList(roundNumber);

                //then
                assertThat(examQuestionList.size()).isEqualTo(2);

                ExamQuestion ret1 = examQuestionList.get(0);
                ExamQuestion ret2 = examQuestionList.get(1);
                //examQuestion1 체크
                assertThat(ret1.getId()).isEqualTo(examQuestion1.getId());

                //examQuestion1 체크
                assertThat(ret2.getId()).isEqualTo(examQuestion2.getId());

                baseCLear();
            }
        }
    }

}
