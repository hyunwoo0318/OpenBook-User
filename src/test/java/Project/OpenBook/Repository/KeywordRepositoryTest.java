package Project.OpenBook.Repository;

import Project.OpenBook.Domain.Category.Domain.Category;
import Project.OpenBook.Domain.Chapter.Domain.Chapter;
import Project.OpenBook.Config.TestQueryDslConfig;
import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import Project.OpenBook.Domain.Category.Repository.CategoryRepository;
import Project.OpenBook.Domain.Chapter.Repo.ChapterRepository;
import Project.OpenBook.Domain.Keyword.Repository.KeywordRepository;
import Project.OpenBook.Domain.Topic.Repo.TopicRepository;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import com.querydsl.core.Tuple;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static Project.OpenBook.Domain.Keyword.Domain.QKeyword.keyword;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestQueryDslConfig.class)
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("KeywordRepository class")
@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
public class KeywordRepositoryTest {
    @Autowired
    EntityManager entityManager;
    @Autowired
    TopicRepository topicRepository;
    @Autowired
    KeywordRepository keywordRepository;
    @Autowired
    ChapterRepository chapterRepository;
    @Autowired
    CategoryRepository categoryRepository;

    private Chapter ch1;
    private Category c1;
    private Topic t1;
    @BeforeAll
    public void baseInit() {
        ch1 = new Chapter("ch1", 1);
        c1 = new Category("c1");

        chapterRepository.save(ch1);
        categoryRepository.save(c1);

        t1 = new Topic("t1", 0, 100, true, true, 0, 0,
                "detai1", ch1, c1);
        topicRepository.save(t1);
    }

    @AfterAll
    public void baseClear(){
        topicRepository.deleteAllInBatch();
        chapterRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
    }

    @Nested
    @DisplayName("queryByNameInTopic() 메서드는")
    public class queryByNameInTopicTest{

        private Keyword k1;
        @Nested
        @DisplayName("해당 주제 내에 해당 이름을 가지는 키워드가 존재한다면")
        public class existKeyword{

            @AfterEach
            public void clear(){
                keywordRepository.deleteAllInBatch();
            }

            @Test
            @DisplayName("해당 키워드를 Optional로 감싸서 리턴한다")
            public void returnKeywordOptional(){
                //given
                init();

                //when
                Optional<Keyword> keywordOptional = keywordRepository.queryByNameInTopic(k1.getName(), t1.getTitle());

                //then
                assertThat(keywordOptional.get()).isEqualTo(k1);
            }
        }

        @Nested
        @DisplayName("해당 주제 내에 해당 이름을 가지는 키워드가 존재하지 않는다면")
        public class notExistKeyword{

            @AfterEach
            public void clear(){
                keywordRepository.deleteAllInBatch();
            }

            @Test
            @DisplayName("null값을 Optional로 감싸서 리턴한다.")
            public void returnKeywordOptionalEmpty(){
                //given
                init();

                //when
                Optional<Keyword> keywordOptional = keywordRepository.queryByNameInTopic("wrong name", t1.getTitle());

                //then
                assertThat(keywordOptional.isEmpty()).isTrue();
            }
        }

        @Nested
        @DisplayName("존재 하지 않는 주제 제목을 입력할 경우")
        public class notExistTopicTitle{

            @AfterEach
            public void clear(){
                keywordRepository.deleteAllInBatch();
            }

            @Test
            @DisplayName("null값을 Optional로 감싸서 리턴한다.")
            public void returnKeywordOptionalEmpty(){
                //given
                init();

                //when
                Optional<Keyword> keywordOptional = keywordRepository.queryByNameInTopic(k1.getName(), "wrong title");

                //then
                assertThat(keywordOptional.isEmpty()).isTrue();
            }
        }

        private void init(){
            k1 = new Keyword("k1", "c1", t1, null);
            keywordRepository.save(k1);
        }
    }

    @Nested
    @DisplayName("queryKeywordsInTopic() 메서드는")
    public class queryKeywordsInTopicTest{

        private Keyword k1,k2;

        @Nested
        @DisplayName("해당 주제에 키워드가 존재하는 경우")
        public class existKeyword{

            @AfterEach
            public void clear(){
                keywordRepository.deleteAllInBatch();
            }
            @Test
            @DisplayName("전체 키워드를 포함한 리스트를 리턴한다.")
            public void returnKeywordList(){
                //given
                init();

                //when
                List<Keyword> keywordList = keywordRepository.queryKeywordsInTopic(t1.getTitle());

                //then
                assertThat(keywordList.size()).isEqualTo(2);
                assertThat(keywordList.containsAll(Arrays.asList(k1, k2))).isTrue();
            }
        }

        @Nested
        @DisplayName("해당 주제에 키워드가 존재하지 않는 경우")
        public class notExistKeyword {

            @Test
            @DisplayName("빈 리스트를 리턴한다.")
            public void returnEmptyList(){
                //given

                //when
                List<Keyword> keywordList = keywordRepository.queryKeywordsInTopic(t1.getTitle());

                //then
                assertThat(keywordList.isEmpty()).isTrue();
            }
        }

        @Nested
        @DisplayName("존재 하지 않는 토픽 제목을 입력할 경우")
        public class notExistTopicTitle{

            @AfterEach
            public void clear(){
                keywordRepository.deleteAllInBatch();
            }
            @Test
            @DisplayName("빈 리스트를 리턴한다.")
            public void returnEmptyList(){
                //given
                init();

                //when
                List<Keyword> keywordList = keywordRepository.queryKeywordsInTopic("wrong title");

                //then
                assertThat(keywordList.isEmpty()).isTrue();
            }
        }

        private void init(){
            k1 = new Keyword("k1", "c1", t1, null);
            k2 = new Keyword("k2", "c2", t1, null);
            keywordRepository.saveAll(Arrays.asList(k1, k2));
        }
    }

    @Nested
    @DisplayName("queryKeywordsInTopicWithLimit() 메서드는")
    public class queryKeywordsInTopicWithLimitTest{
        private Keyword k1,k2,k3;

        @Nested
        @DisplayName("limit값이 특정 주제의 키워드 개수보다 작을 경우")
        public class sizeIsLessThanKeywordNum{

            private int limit = 2;
            @AfterEach
            public void clear(){
                keywordRepository.deleteAllInBatch();
            }
            @Test
            @DisplayName("keyword를 limit값만큼 찾아서 리스트로 리턴한다.")
            public void returnKeywordListInSize(){
                //given
                init();

                //when
                List<Keyword> keywordList = keywordRepository.queryKeywordsInTopicWithLimit(t1.getTitle(), limit);

                //then
                assertThat(keywordList.size()).isEqualTo(limit);
                for (Keyword keyword : keywordList) {
                    //각 키워드가 t1내의 키워드인지 확인
                    assertThat(keyword.getTopic()).isEqualTo(t1);
                }
            }
        }

        @Nested
        @DisplayName("limit값이 특정 주제의 키워드 개수보다 클 경우")
        public class sizeIsBiggerThanKeywordNum{

            private int limit = 4;
            @AfterEach
            public void clear(){
                keywordRepository.deleteAllInBatch();
            }
            @Test
            @DisplayName("특정 주제의 전체 키워드를 리스트로 리턴한다.")
            public void returnTotalKeyword(){
                //given
                init();

                //when
                List<Keyword> keywordList = keywordRepository.queryKeywordsInTopicWithLimit(t1.getTitle(), limit);

                //then
                assertThat(keywordList.size()).isEqualTo(3);
                assertThat(keywordList.containsAll(Arrays.asList(k1, k2, k3))).isTrue();
            }
        }

        @Nested
        @DisplayName("존재하지 않는 주제 제목을 입력하는 경우")
        public class notExistTopicTitle{

            @Test
            @DisplayName("빈 리스트를 리턴한다.")
            public void returnEmptyList(){
                //given

                //when
                List<Keyword> keywordList = keywordRepository.queryKeywordsInTopicWithLimit("wrong title", 1);

                //then
                assertThat(keywordList.isEmpty()).isTrue();
            }
        }

        private void init(){
            k1 = new Keyword("k1", "c1", t1, null);
            k2 = new Keyword("k2", "c2", t1, null);
            k3 = new Keyword("k3", "c3", t1, null);
            keywordRepository.saveAll(Arrays.asList(k1, k2, k3));
        }
    }

    @Nested
    @DisplayName("queryWrongKeywords() 메서드는")
    public class queryWrongKeywordsTest{

        private Keyword k1,k2,k3;
        private Topic t2,t3;

        @Nested
        @DisplayName("limit값이 정답 주제를 제외한 나머지 주제들의 키워드 개수보다 작을 경우")
        public class sizeIsLessThanTotalKeywordNum{

            private int limit = 2;
            @AfterEach
            public void clear(){
                keywordRepository.deleteAllInBatch();
                topicRepository.deleteAllInBatch();
            }
            @Test
            @DisplayName("{keywordName, keywordComment, keywordTopicTitle}를 limit값만큼 찾아서 리스트로 리턴한다.")
            public void returnKeywordListInSize(){
                //given
                init();

                //when
                List<Tuple> result = keywordRepository.queryWrongKeywords(t1.getTitle(), limit);

                //then
                assertThat(result.size()).isEqualTo(limit);
                for (Tuple tuple : result) {
                    String keywordName = tuple.get(keyword.name);
                    String keywordComment = tuple.get(keyword.comment);
                    String topicTitle = tuple.get(keyword.topic.title);

                    assertThat(keywordName).isNotNull();
                    assertThat(keywordComment).isNotNull();
                    assertThat(topicTitle).isNotEqualTo(t1.getTitle());
                }
            }
        }

        @Nested
        @DisplayName("limit값이 정답 주제를 제외한 나머지 키워드의 개수보다 클 경우")
        public class sizeIsBiggerThanKeywordNum{

            private int limit = 4;
            @AfterEach
            public void clear(){
                keywordRepository.deleteAllInBatch();
                topicRepository.deleteAllInBatch();
            }
            @Test
            @DisplayName("나머지 주제의 전체 키워드를 리턴한다.")
            public void returnTotalKeyword(){
                //given
                init();

                //when
                List<Tuple> result = keywordRepository.queryWrongKeywords(t1.getTitle(), limit);

                //then
                assertThat(result.size()).isEqualTo(3);
                for (Tuple tuple : result) {
                    String keywordName = tuple.get(keyword.name);
                    String keywordComment = tuple.get(keyword.comment);
                    String topicTitle = tuple.get(keyword.topic.title);

                    assertThat(keywordName).isNotNull();
                    assertThat(keywordComment).isNotNull();
                    assertThat(topicTitle).isNotEqualTo(t1.getTitle());
                }
            }
        }

        @Nested
        @DisplayName("존재하지 않는 주제 제목을 입력하는 경우")
        public class notExistTopicTitle{

            @Test
            @DisplayName("빈 리스트를 리턴한다.")
            public void returnEmptyList(){
                //given

                //when
                List<Keyword> keywordList = keywordRepository.queryKeywordsInTopicWithLimit("wrong title", 1);

                //then
                assertThat(keywordList.isEmpty()).isTrue();
            }
        }

        private void init(){
            t2 = new Topic("t2", 0, 1, false, false, 0, 0,
                    "detail2", ch1, c1);
            t3 = new Topic("t3", 0, 2, false, false, 0, 0,
                    "detail3", ch1, c1);
            topicRepository.saveAll(Arrays.asList(t2, t3));

            k1 = new Keyword("k1", "c1", t2, null);
            k2 = new Keyword("k2", "c2", t2, null);
            k3 = new Keyword("k3", "c3", t3, null);
            keywordRepository.saveAll(Arrays.asList(k1, k2, k3));
        }
    }


}
