//package Project.OpenBook.Repository;
//
//import Project.OpenBook.Config.TestQueryDslConfig;
//import Project.OpenBook.Constants.Role;
//import Project.OpenBook.Domain.Chapter.Domain.Chapter;
//import Project.OpenBook.Domain.StudyProgress.ChapterProgress.Domain.ChapterProgress;
//import Project.OpenBook.Domain.Customer.Domain.Customer;
//import Project.OpenBook.Domain.Chapter.Repo.ChapterRepository;
//import Project.OpenBook.Domain.StudyProgress.ChapterProgress.Repository.ChapterProgressRepository;
//import Project.OpenBook.Domain.Customer.Repository.CustomerRepository;
//import org.junit.jupiter.api.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.context.annotation.Import;
//
//import javax.persistence.EntityManager;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@DataJpaTest
//@Import(TestQueryDslConfig.class)
//@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
//@DisplayName("ChapterProgressRepository class")
//@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
//public class ChapterProgressRepositoryTest {
//
//    @Autowired
//    EntityManager entityManager;
//    @Autowired
//    ChapterRepository chapterRepository;
//    @Autowired
//    CustomerRepository customerRepository;
//    @Autowired
//    ChapterProgressRepository chapterProgressRepository;
//
//    private Chapter ch1;
//    private Customer c1;
//    @BeforeAll
//    public void baseInit(){
//        ch1 = new Chapter("ch1", 1);
//        c1 = new Customer("c1", "p1", Role.USER);
//
//        chapterRepository.save(ch1);
//        customerRepository.save(c1);
//    }
//
//    @AfterAll
//    public void baseClear(){
//        chapterRepository.deleteAllInBatch();
//        customerRepository.deleteAllInBatch();
//    }
//    @Nested
//    @DisplayName("queryChapterProgress() 메서드는")
//    public class queryChapterProgressTest{
//
//        @Nested
//        @DisplayName("해당 회원과 해당 단원에 대한 progress가 존재한다면")
//        public class existChapterProgress {
//
//            @AfterEach
//            public void clear() {
//                chapterProgressRepository.deleteAllInBatch();
//            }
//            @Test
//            @DisplayName("해당 progress를 Optional로 감싸서 리턴한다.")
//            public void returnProgressOptional(){
//                //given
//                ChapterProgress chapterProgress = new ChapterProgress(c1, ch1);
//                chapterProgressRepository.save(chapterProgress);
//
//                //when
//                Optional<ChapterProgress> chapterProgressOptional = chapterProgressRepository.queryChapterProgress(c1.getId(), ch1.getNumber());
//
//                //then
//                assertThat(chapterProgressOptional.get()).isEqualTo(chapterProgress);
//            }
//        }
//
//        @Nested
//        @DisplayName("해당 회원과 해당 단원에 대한 progress가 존재하지 않는다면")
//        public class notExistChapterProgress{
//
//            @Test
//            @DisplayName("null값을 Optional로 감싸서 리턴한다.")
//            public void returnNullOptional(){
//                //given
//
//                //when
//                Optional<ChapterProgress> chapterProgressOptional = chapterProgressRepository.queryChapterProgress(c1.getId(), ch1.getNumber());
//
//                //then
//                assertThat(chapterProgressOptional.isEmpty()).isTrue();
//            }
//        }
//    }
//}
