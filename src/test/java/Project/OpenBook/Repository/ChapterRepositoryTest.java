package Project.OpenBook.Repository;

import Project.OpenBook.Chapter.Domain.Chapter;
import Project.OpenBook.Config.TestQueryDslConfig;
import Project.OpenBook.Constants.ContentConst;
import Project.OpenBook.Constants.Role;
import Project.OpenBook.Domain.*;
import Project.OpenBook.Chapter.Repo.ChapterRepository;
import Project.OpenBook.Repository.chaptersection.ChapterSectionRepository;
import Project.OpenBook.Repository.customer.CustomerRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestQueryDslConfig.class)
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("ChpaterRepository class")
public class ChapterRepositoryTest {

    @Autowired
    ChapterRepository chapterRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    ChapterSectionRepository chapterSectionRepository;
    @Autowired
    TestEntityManager entityManager;

    @Nested
    @Transactional
    @DisplayName("findOneByNumber() 메서드는")
    public class findOneTest{

        private Chapter ch1, ch2;

        @Nested
        @DisplayName("해당 번호를 가지고 있는 단원이 존재하지 않는다면")
        public class notExistChapter{
            @AfterEach
            public void clear(){
                chapterRepository.deleteAllInBatch();
            }

            @DisplayName("null값을 Optional로 감싸서 리턴한다.")
            @Test
            public void returnOptionalEmpty() {
                //given
                initChapter();

                //when
                Optional<Chapter> chapterOptional = chapterRepository.findOneByNumber(3);

                //then
                assertThat(chapterOptional.isEmpty()).isTrue();
            }
        }

        @Nested
        @DisplayName("해당 번호를 가지고 있는 단원이 존재한다면")
        public class existChapter{
            @DisplayName("해당 chapter를 Optional로 감싸서 리턴한다.")
            @Test
            public void returnChapterOptional() {
                //given
                initChapter();

                //when
                Optional<Chapter> chapterOptional = chapterRepository.findOneByNumber(ch1.getNumber());

                //then
                assertThat(chapterOptional.get()).isEqualTo(ch1);
            }
        }

        private void initChapter() {
            ch1 = new Chapter("title1", 1);
            entityManager.persist(ch1);

            ch2 = new Chapter("title2", 2);
            entityManager.persist(ch2);
        }
    }

    @Nested
    @DisplayName("queryChapterWithProgress() 메서드는")
    public class queryChapterWithProgressTest {

        private Customer customer;
        private Chapter ch1,ch2;
        @Nested
        @DisplayName("모든 단원에 대해서 chapterProgress가 존재한다면")
        public class existAllChapterSection {
            @BeforeEach
            public void clear() {
                chapterRepository.deleteAllInBatch();
                customerRepository.deleteAllInBatch();
            }

            @Test
            @DisplayName("전체 단원의 수만큼 <chapter, chapterProgress> Map을 리턴한다.")
            public void existAllChapterProgress(){
                String ch1Progress = ContentConst.TIME_FLOW_STUDY.getName();
                String ch2Progress = ContentConst.NOT_STARTED.getName();

                //given
                init();
                ChapterProgress cp1 = new ChapterProgress(customer, ch1, 0, ch1Progress);
                ChapterProgress cp2 = new ChapterProgress(customer, ch2, 0, ch2Progress);

                entityManager.persist(cp1);
                entityManager.persist(cp2);

                //when
                Map<Chapter, ChapterProgress> resultMap = chapterRepository.queryChapterWithProgress(customer.getId());

                //then

                //단원의 개수만큼 쿼리 결과가 생성되었는지 확인
                assertThat(resultMap.keySet().size()).isEqualTo(2);

                //1단원
                ChapterProgress chapterProgress1 = resultMap.get(ch1);
                assertThat(chapterProgress1).isEqualTo(cp1);

                //2단원
                ChapterProgress chapterProgress2 = resultMap.get(ch2);
                assertThat(chapterProgress2).isEqualTo(cp2);
            }


        }

        @Nested
        @DisplayName("특정 단원에 대해서 chapterProgress가 존재하지 않는다면")
        public class notExistSomeChapterSection {
            @BeforeEach
            public void clear() {
                chapterSectionRepository.deleteAllInBatch();
                customerRepository.deleteAllInBatch();
                chapterRepository.deleteAllInBatch();
            }

            @DisplayName("해당 단원의 progress는 null로 리턴한다.")
            @Transactional
            @Test
            public void NotExistChapterProgress() {
                //given
                init();

                //when
                Map<Chapter, ChapterProgress> resultMap = chapterRepository.queryChapterWithProgress(customer.getId());

                //단원의 개수만큼 쿼리 결과가 생성되었는지 확인
                assertThat(resultMap.keySet().size()).isEqualTo(2);

                //1단원
                ChapterProgress chapterProgress1 = resultMap.get(ch1);
                assertThat(chapterProgress1).isNull();

                //2단원
                ChapterProgress chapterProgress2 = resultMap.get(ch2);
                assertThat(chapterProgress2).isNull();
            }

        }

        private void init(){
            customer = new Customer("customer1", "customer1", Role.USER);
            entityManager.persist(customer);

            ch1 = new Chapter("title1", 1);
            ch2 = new Chapter("title2", 2);
            entityManager.persist(ch1);
            entityManager.persist(ch2);
        }



    }

}
