//package Project.OpenBook.Repository;
//
//import Project.OpenBook.Domain.Category.Domain.Category;
//import Project.OpenBook.Domain.Chapter.Domain.Chapter;
//import Project.OpenBook.Config.TestQueryDslConfig;
//import Project.OpenBook.Constants.Role;
//import Project.OpenBook.Domain.Customer.Domain.Customer;
//import Project.OpenBook.Domain.Category.Repository.CategoryRepository;
//import Project.OpenBook.Domain.Chapter.Repo.ChapterRepository;
//import Project.OpenBook.Domain.Customer.Repository.CustomerRepository;
//import Project.OpenBook.Domain.Topic.Repo.TopicRepository;
//import Project.OpenBook.Domain.StudyProgress.TopicProgress.Repository.TopicProgressRepository;
//import Project.OpenBook.Domain.Topic.Domain.Topic;
//import Project.OpenBook.Domain.StudyProgress.TopicProgress.Domain.TopicProgress;
//import org.junit.jupiter.api.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.context.annotation.Import;
//
//import javax.persistence.EntityManager;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//
//@DataJpaTest
//@Import(TestQueryDslConfig.class)
//@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
//@DisplayName("TopicProgressRepository class")
//@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
//public class TopicProgressRepositoryTest {
//
//    @Autowired
//    EntityManager entityManager;
//    @Autowired
//    ChapterRepository chapterRepository;
//    @Autowired
//    CategoryRepository categoryRepository;
//    @Autowired
//    CustomerRepository customerRepository;
//    @Autowired
//    TopicRepository topicRepository;
//    @Autowired
//    TopicProgressRepository topicProgressRepository;
//
//    private Chapter ch1;
//    private Category category1;
//    private Topic t1,t2;
//    private Customer c1;
//
//    @BeforeAll
//    public void baseInit(){
//        ch1 = new Chapter("ch1", 1);
//        category1 = new Category("c1");
//        chapterRepository.save(ch1);
//        categoryRepository.save(category1);
//
//        c1 = new Customer("c1", "p1", Role.USER);
//        customerRepository.save(c1);
//
//        t1 = new Topic("t1", 0, 1, false, false, 0, 0,
//                "detail1", ch1, category1);
//        t2 = new Topic("t2", 0, 1, false, false, 0, 0,
//                "detail2", ch1, category1);
//        topicRepository.save(t1);
//        topicRepository.save(t2);
//    }
//
//    @AfterAll
//    public void baseClear(){
//        topicRepository.deleteAllInBatch();
//        categoryRepository.deleteAllInBatch();
//        chapterRepository.deleteAllInBatch();
//        customerRepository.deleteAllInBatch();
//    }
//
//    @Nested
//    @DisplayName("queryTopicProgress() 메서드는")
//    public class queryTopicProgressTest{
//
//        @Nested
//        @DisplayName("회원id와 주제 제목에 맞는 topicProgress가 존재하면")
//        public class existTopicProgress{
//
//            @AfterEach
//            public void clear(){
//                topicProgressRepository.deleteAllInBatch();
//            }
//            @Test
//            @DisplayName("해당 topicProgress를 Optional로 감싸서 리턴한다.")
//            public void returnTopicProgressOptional() {
//                //given
//                TopicProgress topicProgress = new TopicProgress(c1, t1);
//                topicProgressRepository.save(topicProgress);
//
//                //when
//                Optional<TopicProgress> topicProgressOptional = topicProgressRepository.queryTopicProgress(c1.getId(), t1.getTitle());
//
//                //then
//                assertThat(topicProgressOptional.get()).isEqualTo(topicProgress);
//            }
//        }
//
//        @Nested
//        @DisplayName("회원id와 주제 제목에 맞는 topicProgress가 존재하지 않는다면")
//        public class notExistTopicProgress{
//
//            @Test
//            @DisplayName("null값을 Optional로 감싸서 리턴한다.")
//            public void returnNullOptional(){
//                //given
//
//                //when
//                Optional<TopicProgress> topicProgressOptional = topicProgressRepository.queryTopicProgress(c1.getId(), t1.getTitle());
//
//                //then
//                assertThat(topicProgressOptional.isEmpty()).isTrue();
//            }
//        }
//    }
//
//    @Nested
//    @DisplayName("queryTopicProgresses() 메서드는")
//    public class queryTopicProgressesTest{
//
//        private TopicProgress tp1, tp2;
//
//        @Nested
//        @DisplayName("해당 단원에 topicProgress가 존재한다면")
//        public class existTopicProgress {
//
//            @AfterEach
//            public void clear(){
//                topicProgressRepository.deleteAllInBatch();
//            }
//            @Test
//            @DisplayName("전체 topicProgress를 하나의 리스트로 리턴한다.")
//            public void returnTopicProgressList() {
//                //given
//                init();
//
//                //when
//                List<TopicProgress> topicProgressList = topicProgressRepository.queryTopicProgresses(c1.getId(), ch1.getNumber());
//
//                //then
//                assertThat(topicProgressList.size()).isEqualTo(2);
//                assertThat(topicProgressList.containsAll(Arrays.asList(tp1, tp2))).isTrue();
//            }
//        }
//
//        @Nested
//        @DisplayName("해당 단원에 topicProgress가 존재하지 않는 경우")
//        public class notExistTopicProgress{
//
//            @Test
//            @DisplayName("빈 리스트를 리턴한다.")
//            public void returnEmptyList() {
//                //given
//
//                //when
//                List<TopicProgress> topicProgressList = topicProgressRepository.queryTopicProgresses(c1.getId(), ch1.getNumber());
//
//                //then
//                assertThat(topicProgressList.isEmpty()).isTrue();
//            }
//        }
//
//        private void init(){
//            tp1 = new TopicProgress(c1, t1);
//            tp2 = new TopicProgress(c1, t2);
//            topicProgressRepository.saveAll(Arrays.asList(tp1, tp2));
//        }
//    }
//}
