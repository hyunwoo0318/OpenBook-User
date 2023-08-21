//package Project.OpenBook.Repository;
//
//import Project.OpenBook.Config.TestQueryDslConfig;
//import Project.OpenBook.Constants.ContentConst;
//import Project.OpenBook.Constants.Role;
//import Project.OpenBook.Domain.*;
//import Project.OpenBook.Repository.chapter.ChapterRepository;
//import Project.OpenBook.Repository.chaptersection.ChapterSectionRepository;
//import Project.OpenBook.Repository.customer.CustomerRepository;
//import com.querydsl.core.Tuple;
//import org.junit.jupiter.api.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
//import org.springframework.context.annotation.Import;
//
//import javax.transaction.Transactional;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import static Project.OpenBook.Domain.QChapter.chapter;
//import static Project.OpenBook.Domain.QChapterProgress.chapterProgress;
//import static org.junit.jupiter.api.Assertions.*;
//
//@DataJpaTest
//@Import(TestQueryDslConfig.class)
//@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
//@DisplayName("ChpaterRepository class")
//public class ChapterRepositoryTest {
//
//    @Autowired
//    ChapterRepository chapterRepository;
//    @Autowired
//    CustomerRepository customerRepository;
//    @Autowired
//    ChapterSectionRepository chapterSectionRepository;
//    @Autowired
//    TestEntityManager entityManager;
//
//    @Nested
//    @DisplayName("findAll() 메서드는")
//    public class findAllTest{
//
//        @AfterEach
//        public void clear(){
//            chapterRepository.deleteAllInBatch();
//        }
//
//        @Test
//        @DisplayName("chapterRepository가 비어있다면")
//        public void findAllNoInstance() {
//            //given
//
//            //when
//            List<Chapter> chapterList = chapterRepository.findAll();
//
//            //then
//            assertTrue(chapterList.isEmpty(), "빈 List를 리턴한다.");
//        }
//
//        @Test
//        @DisplayName("chapterRepository에 chapter가 존재한다면")
//        public void findAllSuccess() {
//            //given
//            Chapter ch1 = new Chapter("ch1", 1);
//            entityManager.persist(ch1);
//            Chapter ch2 = new Chapter("ch2", 2);
//            entityManager.persist(ch2);
//
//            //when
//            List<Chapter> chapterList = chapterRepository.findAll();
//
//            //then
//            assertEquals(chapterList.size(), 2);
//            assertTrue(chapterList.containsAll(Arrays.asList(ch1, ch2)), "chapter 전체를 하나의 리스트로 리턴한다.");
//        }
//
//    }
//
//    @Nested
//    @Transactional
//    @DisplayName("findOneByNumber() 메서드는")
//    public class findOneTest{
//
//        private Chapter ch1, ch2;
//
//        @AfterEach
//        public void clear(){
//            chapterRepository.deleteAllInBatch();
//        }
//
//        @DisplayName("해당 번호를 가지고 있는 단원이 존재하지 않는다면")
//        @Test
//        public void chapterNotExist() {
//            //given
//            initChapter();
//
//            //when
//            Optional<Chapter> chapterOptional = chapterRepository.findOneByNumber(3);
//
//            //then
//            assertTrue(chapterOptional.isEmpty(), "null값을 Optional로 감싸서 리턴한다.");
//        }
//
//        @DisplayName("해당 번호를 가지고 있는 단원이 존재한다면")
//        @Test
//        public void chapterExist() {
//            //given
//            initChapter();
//
//            //when
//            Optional<Chapter> chapterOptional = chapterRepository.findOneByNumber(ch1.getNumber());
//
//            //then
//            assertEquals(chapterOptional.get(),ch1, "해당 번호를 가지는 단원을 리턴한다.");
//        }
//
//        private void initChapter() {
//            ch1 = new Chapter("title1", 1);
//            entityManager.persist(ch1);
//
//            ch2 = new Chapter("title2", 2);
//            entityManager.persist(ch2);
//        }
//    }
////
////    @Nested
////    @DisplayName("queryChapterUserDtos() 메서드는")
////    public class queryChapterUserDtosTest {
////
////        private Customer customer;
////        private Chapter ch1,ch2;
////        @Nested
////        @DisplayName("모든 단원에 대해서 chapterProgress가 존재한다면")
////        public class existAllChapterSection {
////            @BeforeEach
////            public void clear() {
////                chapterSectionRepository.deleteAllInBatch();
////                customerRepository.deleteAllInBatch();
////                chapterRepository.deleteAllInBatch();
////            }
////
////            @Test
////            @DisplayName("전체 단원의 수만큼 {단원제목, 단원번호, progress} 객체를 리턴한다.")
////            public void existAllChapterProgress(){
////                String ch1Progress = ContentConst.TIME_FLOW_QUESTION;
////                String ch2Progress = ContentConst.NOT_STARTED;
////
////                //given
////                init();
////                ChapterSection cp1 = new ChapterSection(customer, ch1, ch1Progress);
////                ChapterSection cp2 = new ChapterSection(customer, ch2, ch2Progress);
////                entityManager.persist(cp1);
////                entityManager.persist(cp2);
////
////                //when
////                List<Tuple> tuples = chapterRepository.queryChapterUserDtos(customer.getId());
////
////                //then
////
////                //단원의 개수만큼 쿼리 결과가 생성되었는지 확인
////                assertEquals(tuples.size(),2, "전체 단원의 수만큼의 객체를 리턴한다.");
////                Tuple ch1Tuple = tuples.get(0);
////                Tuple ch2Tuple = tuples.get(1);
////
////                //1단원
////                assertEquals(ch1Tuple.get(chapter.title), ch1.getTitle());
////                assertEquals(ch1Tuple.get(chapter.number), ch1.getNumber());
////                assertEquals(ch1Tuple.get(chapterProgress.progress), ch1Progress);
////
////                //2단원
////                assertEquals(ch2Tuple.get(chapter.title), ch2.getTitle());
////                assertEquals(ch2Tuple.get(chapter.number), ch2.getNumber());
////                assertEquals(ch2Tuple.get(chapterProgress.progress), ch2Progress);
////            }
////
////            private void init(){
////                customer = new Customer("customer1", "customer1", Role.USER);
////                entityManager.persist(customer);
////
////                ch1 = new Chapter("title1", 1);
////                ch2 = new Chapter("title2", 2);
////                entityManager.persist(ch1);
////                entityManager.persist(ch2);
////            }
////        }
////
////        @Nested
////        @DisplayName("특정 단원에 대해서 chapterProgress가 존재하지 않는다면")
////        public class notExistSomeChapterSection {
////            @BeforeEach
////            public void clear() {
////                chapterSectionRepository.deleteAllInBatch();
////                customerRepository.deleteAllInBatch();
////                chapterRepository.deleteAllInBatch();
////            }
////
////            @DisplayName("해당 단원의 progress는 null로 리턴한다.")
////            @Transactional
////            @Test
////            public void NotExistChapterProgress() {
////                //given
////                init();
////
////                //when
////                List<Tuple> tuples = chapterRepository.queryChapterUserDtos(customer.getId());
////
////                //단원의 개수만큼 쿼리 결과가 생성되었는지 확인
////                assertEquals(tuples.size(),2);
////                Tuple ch1Tuple = tuples.get(0);
////                Tuple ch2Tuple = tuples.get(1);
////
////                //1단원
////                assertEquals(ch1Tuple.get(chapter.title), ch1.getTitle());
////                assertEquals(ch1Tuple.get(chapter.number), ch1.getNumber());
////                assertNull(ch1Tuple.get(chapterProgress.progress));
////
////                //2단원
////                assertEquals(ch2Tuple.get(chapter.title), ch2.getTitle());
////                assertEquals(ch2Tuple.get(chapter.number), ch2.getNumber());
////                assertNull(ch2Tuple.get(chapterProgress.progress));
////            }
////
////            private void init(){
////                customer = new Customer("customer1", "customer1", Role.USER);
////                entityManager.persist(customer);
////
////                ch1 = new Chapter("title1", 1);
////                ch2 = new Chapter("title2", 2);
////                entityManager.persist(ch1);
////                entityManager.persist(ch2);
////            }
////
////        }
////
////
////
////    }
//
//}
