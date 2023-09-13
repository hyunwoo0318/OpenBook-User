//package Project.OpenBook.Repository;
//
//import Project.OpenBook.Config.TestQueryDslConfig;
//import Project.OpenBook.Constants.Role;
//import Project.OpenBook.Domain.Chapter.Domain.Chapter;
//import Project.OpenBook.Domain.StudyProgress.ChapterSection.Domain.ChapterSection;
//import Project.OpenBook.Domain.Customer.Domain.Customer;
//import Project.OpenBook.Domain.Chapter.Repo.ChapterRepository;
//import Project.OpenBook.Domain.StudyProgress.ChapterSection.Repository.ChapterSectionRepository;
//import Project.OpenBook.Domain.Customer.Repository.CustomerRepository;
//import org.junit.jupiter.api.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.context.annotation.Import;
//
//import javax.persistence.EntityManager;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import static Project.OpenBook.Constants.ContentConst.CHAPTER_COMPLETE_QUESTION;
//import static Project.OpenBook.Constants.ContentConst.CHAPTER_INFO;
//import static Project.OpenBook.Constants.StateConst.LOCKED;
//import static Project.OpenBook.Constants.StateConst.OPEN;
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//
//@DataJpaTest
//@Import(TestQueryDslConfig.class)
//@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
//@DisplayName("ChapterSectionRepository class")
//@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
//public class ChapterSectionRepositoryTest {
//    @Autowired
//    EntityManager entityManager;
//    @Autowired
//    ChapterRepository chapterRepository;
//    @Autowired
//    CustomerRepository customerRepository;
//    @Autowired
//    ChapterSectionRepository chapterSectionRepository;
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
//
//    @Nested
//    @DisplayName("queryChapterSections() 메서드는")
//    public class queryChapterSectionsTest{
//
//        ChapterSection cs1,cs2;
//
//        @Nested
//        @DisplayName("해당 단원에 chapterSection이 존재하면")
//        public class existChapterSection{
//
//            @AfterEach
//            public void clear(){
//                chapterSectionRepository.deleteAllInBatch();
//            }
//            @Test
//            @DisplayName("해당 단원의 모든 chapterSection을 하나의 리스트로 리턴한다.")
//            public void returnTotalChapterSectionInList() {
//                //given
//                init();
//
//                //when
//                List<ChapterSection> chapterSectionList = chapterSectionRepository.queryChapterSections(c1.getId(), ch1.getNumber());
//
//                //then
//                assertThat(chapterSectionList.size()).isEqualTo(2);
//                assertThat(chapterSectionList.containsAll(Arrays.asList(cs1, cs2))).isTrue();
//            }
//        }
//
//        @Nested
//        @DisplayName("입력받은 회원id, 단원 번호에 맞는 chapterSection이 존재하지 않는경우")
//        public class notExistChapterSection{
//
//            @Test
//            @DisplayName("빈 리스트를 리턴한다.")
//            public void returnEmptyList(){
//                //given
//
//                //when
//                List<ChapterSection> chapterSectionList = chapterSectionRepository.queryChapterSections(c1.getId(), ch1.getNumber());
//
//                //then
//                assertThat(chapterSectionList.isEmpty()).isTrue();
//            }
//        }
//
//
//
//        private void init(){
//            cs1 = new ChapterSection(c1, ch1, CHAPTER_INFO.getName(), OPEN.getName());
//            cs2 = new ChapterSection(c1, ch1, CHAPTER_COMPLETE_QUESTION.getName(), LOCKED.getName());
//            chapterSectionRepository.saveAll(Arrays.asList(cs1, cs2));
//        }
//    }
//
//    @Nested
//    @DisplayName("queryChapterSection() 메서드는")
//    public class queryChapterSectionTest{
//
//        private ChapterSection cs1;
//
//        @Nested
//        @DisplayName("입력받은 customerId, chapterNum, content와 일치하는 chapterSection이 존재한다면")
//        public class existChapterSection {
//
//            @AfterEach
//            public void clear(){
//                chapterSectionRepository.deleteAllInBatch();
//            }
//            @Test
//            @DisplayName("해당 chapterSection을 Optional로 감싸서 리턴한다.")
//            public void returnChapterSectionOptional(){
//                //given
//                init();
//
//                //when
//                Optional<ChapterSection> chapterSectionOptional
//                        = chapterSectionRepository.queryChapterSection(c1.getId(), ch1.getNumber(), CHAPTER_INFO.getName());
//
//                //then
//                assertThat(chapterSectionOptional.get()).isEqualTo(cs1);
//            }
//        }
//
//        @Nested
//        @DisplayName("입력받은 customerId, chapterNum, content와 일치하는 chapterSection이 존재하지 않는다면")
//        public class notExistChapterSection{
//            @Test
//            @DisplayName("null값을 Optional로 감싸서 리턴한다.")
//            public void returnNullOptional(){
//                //given
//
//                //when
//                Optional<ChapterSection> chapterSectionOptional
//                        = chapterSectionRepository.queryChapterSection(c1.getId(), ch1.getNumber(), CHAPTER_INFO.getName());
//
//                //then
//                assertThat(chapterSectionOptional.isEmpty()).isTrue();
//            }
//        }
//
//        private void init(){
//            cs1 = new ChapterSection(c1, ch1, CHAPTER_INFO.getName(), OPEN.getName());
//            chapterSectionRepository.save(cs1);
//        }
//    }
//
//}
