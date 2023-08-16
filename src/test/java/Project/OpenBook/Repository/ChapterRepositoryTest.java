package Project.OpenBook.Repository;

import Project.OpenBook.Config.TestQueryDslConfig;
import Project.OpenBook.Constants.ProgressConst;
import Project.OpenBook.Constants.Role;
import Project.OpenBook.Domain.*;
import Project.OpenBook.Repository.chapter.ChapterRepository;
import Project.OpenBook.Repository.chapterprogress.ChapterProgressRepository;
import Project.OpenBook.Repository.customer.CustomerRepository;
import com.querydsl.core.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.convert.DataSizeUnit;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static Project.OpenBook.Domain.QChapter.chapter;
import static Project.OpenBook.Domain.QChapterProgress.chapterProgress;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@Import(TestQueryDslConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ChapterRepositoryTest {

    @Autowired
    ChapterRepository chapterRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    ChapterProgressRepository chapterProgressRepository;
    @Autowired
    TestEntityManager entityManager;

    @Nested
    @DisplayName("findAll() Test")
    public class findAllTest{

        @Test
        @DisplayName("repo가 비어있는 경우")
        public void findAllNoInstance() {
            //given

            //when
            List<Chapter> chapterList = chapterRepository.findAll();

            //then
            assertThat(chapterList.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("일반적인 경우")
        public void findAllSuccess() {
            //given
            Chapter ch1 = new Chapter("ch1", 1);
            entityManager.persist(ch1);
            Chapter ch2 = new Chapter("ch2", 2);
            entityManager.persist(ch2);

            //when
            List<Chapter> chapterList = chapterRepository.findAll();

            //then
            assertThat(chapterList.size()).isEqualTo(2);
            assertThat(chapterList.containsAll(Arrays.asList(ch1, ch2))).isTrue();
        }

    }

    @Nested
    @DisplayName("findOneByNumber() Test")
    public class findOneTest{

        private Chapter ch1, ch2;

        @DisplayName("해당 번호를 가지고 있는 단원이 존재하지 않는 경우")
        @Test
        public void chapterNotExist() {
            //given
            initChapter();

            //when
            Optional<Chapter> chapterOptional = chapterRepository.findOneByNumber(3);

            //then
            assertThat(chapterOptional.isEmpty()).isTrue();
        }

        @DisplayName("해당 번호를 가지고 있는 단원이 존재하는 경우")
        @Test
        public void chapterExist() {
            //given
            initChapter();

            //when
            Optional<Chapter> chapterOptional = chapterRepository.findOneByNumber(ch1.getNumber());

            //then
            assertThat(chapterOptional.isPresent()).isTrue();
            Chapter findChapter = chapterOptional.get();
            assertThat(findChapter.equals(ch1));
        }

        private void initChapter() {
            ch1 = new Chapter("title1", 1);
            entityManager.persist(ch1);

            ch2 = new Chapter("title2", 2);
            entityManager.persist(ch2);


        }
    }

    @Nested
    @DisplayName("queryChapterUserDtos() Test")
    public class queryChapterUserDtosTest {

        private Customer customer;
        private Chapter ch1,ch2;

        @DisplayName("모든 단원에 대해서 chapterProgress가 존재하는 상황")
        @Test
        public void existAllChapterProgress(){
            String ch1Progress = ProgressConst.TIME_FLOW_QUESTION;
            String ch2Progress = ProgressConst.NOT_STARTED;

            //given
            init();
            ChapterProgress cp1 = new ChapterProgress(customer, ch1, ch1Progress);
            ChapterProgress cp2 = new ChapterProgress(customer, ch2, ch2Progress);
            entityManager.persist(cp1);
            entityManager.persist(cp2);

            //when
            List<Tuple> tuples = chapterRepository.queryChapterUserDtos(customer.getId());

            //then

            //단원의 개수만큼 쿼리 결과가 생성되었는지 확인
            assertThat(tuples.size()).isEqualTo(2);
            Tuple ch1Tuple = tuples.get(0);
            Tuple ch2Tuple = tuples.get(1);

            //1단원
            assertThat(ch1Tuple.get(chapter.title)).isEqualTo(ch1.getTitle());
            assertThat(ch1Tuple.get(chapter.number)).isEqualTo(ch1.getNumber());
            assertThat(ch1Tuple.get(chapterProgress.progress)).isEqualTo(ch1Progress);

            //2단원
            assertThat(ch2Tuple.get(chapter.title)).isEqualTo(ch2.getTitle());
            assertThat(ch2Tuple.get(chapter.number)).isEqualTo(ch2.getNumber());
            assertThat(ch2Tuple.get(chapterProgress.progress)).isEqualTo(ch2Progress);
        }

        @DisplayName("chapterProgress가 존재하지 않는 경우")
        @Test
        public void NotExistChapterProgress() {
            //given
            init();

            //when
            List<Tuple> tuples = chapterRepository.queryChapterUserDtos(customer.getId());

            //단원의 개수만큼 쿼리 결과가 생성되었는지 확인
            assertThat(tuples.size()).isEqualTo(2);
            Tuple ch1Tuple = tuples.get(0);
            Tuple ch2Tuple = tuples.get(1);

            //1단원
            assertThat(ch1Tuple.get(chapter.title)).isEqualTo(ch1.getTitle());
            assertThat(ch1Tuple.get(chapter.number)).isEqualTo(ch1.getNumber());
            assertThat(ch1Tuple.get(chapterProgress.progress)).isNull();

            //2단원
            assertThat(ch2Tuple.get(chapter.title)).isEqualTo(ch2.getTitle());
            assertThat(ch2Tuple.get(chapter.number)).isEqualTo(ch2.getNumber());
            assertThat(ch2Tuple.get(chapterProgress.progress)).isNull();
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
