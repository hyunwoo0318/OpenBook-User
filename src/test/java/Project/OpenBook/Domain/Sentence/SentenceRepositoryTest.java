package Project.OpenBook.Domain.Sentence;

import Project.OpenBook.Config.TestQueryDslConfig;
import Project.OpenBook.Domain.Sentence.Domain.Sentence;
import Project.OpenBook.Domain.Sentence.Repository.SentenceRepository;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import Project.OpenBook.Domain.Topic.Repo.TopicRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestQueryDslConfig.class)
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("SentenceRepository class")
public class SentenceRepositoryTest {

    @Autowired
    SentenceRepository sentenceRepository;

    @Autowired
    TopicRepository topicRepository;

    private Topic t1,t2,t3;

    public void baseSetting(){
        t1 = new Topic("t1");
        t2 = new Topic("t2");
        t3 = new Topic("t3");
        topicRepository.saveAll(Arrays.asList(t1, t2, t3));
    }

    public void baseClear(){
        sentenceRepository.deleteAllInBatch();
        topicRepository.deleteAllInBatch();
    }

    @Nested
    @DisplayName("queryByTopicTitle() 메서드는")
    public class queryByTopicTitleTest{

        @Nested
        @DisplayName("토픽 제목과 쿼리할 문장의 개수를 입력하면")
        public class inputTopicTitleLimit{
            @Test
            @DisplayName("해당 토픽에 limit만큼 랜덤하게 문장 리스트를 리턴한다.")
            public void returnRandomSentenceList_sizeLimit(){
                //given
                baseSetting();
                for (int i = 1; i <= 5; i++) {
                    Sentence sentence = new Sentence("name" + i, t1);
                    sentenceRepository.save(sentence);
                }
                for (int i = 10; i <= 15; i++) {
                    Sentence sentence = new Sentence("name" + i, t2);
                    sentenceRepository.save(sentence);
                }


                //when
                List<Sentence> sentenceList =
                        sentenceRepository.queryByTopicTitle(t1.getTitle(), 3);

                //then
                assertThat(sentenceList.size()).isEqualTo(3);
                for (Sentence sentence : sentenceList) {
                    assertThat(sentence.getTopic().getTitle()).isEqualTo(t1.getTitle());
                }

                baseClear();
            }

            @Nested
            @DisplayName("limit보다 전체 문장의 개수가 적은 경우")
            public class lessSentenceNumThanLimit{

                @Test
                @DisplayName("해당 토픽의 전체 문장을 리턴한다.")
                public void returnTopicTotalSentence() {
                    //given
                    baseSetting();
                    for (int i = 1; i <= 5; i++) {
                        Sentence sentence = new Sentence("name" + i, t1);
                        sentenceRepository.save(sentence);
                    }
                    for (int i = 10; i <= 15; i++) {
                        Sentence sentence = new Sentence("name" + i, t2);
                        sentenceRepository.save(sentence);
                    }

                    //when
                    List<Sentence> sentenceList =
                            sentenceRepository.queryByTopicTitle(t1.getTitle(), 7);

                    //then
                    assertThat(sentenceList.size()).isEqualTo(5);
                    for (Sentence sentence : sentenceList) {
                        assertThat(sentence.getTopic().getTitle()).isEqualTo(t1.getTitle());
                    }

                    baseClear();
                }
            }

            @Nested
            @DisplayName("해당 토픽 제목을 가진 토픽이 존재하지 않을 경우")
            public class notExistTopic {

                @Test
                @DisplayName("빈 리스트를 리턴한다.")
                public void returnEmptyList(){
                    //given
                    baseSetting();
                    for (int i = 1; i <= 5; i++) {
                        Sentence sentence = new Sentence("name" + i, t1);
                        sentenceRepository.save(sentence);
                    }
                    for (int i = 10; i <= 15; i++) {
                        Sentence sentence = new Sentence("name" + i, t2);
                        sentenceRepository.save(sentence);
                    }

                    //when
                    List<Sentence> sentenceList =
                            sentenceRepository.queryByTopicTitle("wrong-title", 3);

                    //then
                    assertThat(sentenceList.isEmpty()).isTrue();

                    baseClear();
                }
            }
        }
    }

    @Nested
    @DisplayName("queryWrongSentences() 메서드는")
    public class queryWrongSentencesTest{

        @Nested
        @DisplayName("정답 토픽 제목과 쿼리할 문장의 개수를 입력하면")
        public class inputAnswerTopicTitleLimit{
            @Test
            @DisplayName("정답 토픽을 제외한 나머지에 limit만큼 랜덤하게 문장 리스트를 리턴한다.")
            public void returnRandomSentenceList_sizeLimit(){
                //given
                baseSetting();
                for (int i = 1; i <= 5; i++) {
                    Sentence sentence = new Sentence("name" + i, t1);
                    sentenceRepository.save(sentence);
                }
                for (int i = 10; i <= 15; i++) {
                    Sentence sentence = new Sentence("name" + i, t2);
                    sentenceRepository.save(sentence);
                }

                for (int i = 100; i <= 150; i++) {
                    Sentence sentence = new Sentence("name" + i, t3);
                    sentenceRepository.save(sentence);
                }


                //when
                List<Sentence> sentenceList =
                        sentenceRepository.queryWrongSentences(t1.getTitle(), 3);

                //then
                assertThat(sentenceList.size()).isEqualTo(3);
                for (Sentence sentence : sentenceList) {
                    assertThat(sentence.getTopic().getTitle()).isNotEqualTo(t1.getTitle());
                }

                baseClear();
            }

            @Nested
            @DisplayName("limit보다 다른 토픽의 총 문장 개수가 적은 경우")
            public class lessSentenceNumThanLimit{

                @Test
                @DisplayName("다른 토픽들의 전체 문장을 리턴한다.")
                public void returnTopicTotalSentence() {
                    //given
                    baseSetting();
                    for (int i = 1; i <= 5; i++) {
                        Sentence sentence = new Sentence("name" + i, t1);
                        sentenceRepository.save(sentence);
                    }
                    for (int i = 11; i <= 15; i++) {
                        Sentence sentence = new Sentence("name" + i, t2);
                        sentenceRepository.save(sentence);
                    }

                    for (int i = 101; i <= 105; i++) {
                        Sentence sentence = new Sentence("name" + i, t3);
                        sentenceRepository.save(sentence);
                    }

                    //when
                    List<Sentence> sentenceList =
                            sentenceRepository.queryWrongSentences(t1.getTitle(), 11);

                    //then
                    assertThat(sentenceList.size()).isEqualTo(10);
                    for (Sentence sentence : sentenceList) {
                        assertThat(sentence.getTopic().getTitle()).isNotEqualTo(t1.getTitle());
                    }

                    baseClear();
                }
            }

        }
    }
}
