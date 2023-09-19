package Project.OpenBook.Domain.Chapter;

import Project.OpenBook.Domain.Chapter.Domain.Chapter;
import Project.OpenBook.Domain.StudyProgress.ChapterProgress.Domain.ChapterProgress;
import Project.OpenBook.Config.TestQueryDslConfig;
import Project.OpenBook.Constants.ContentConst;
import Project.OpenBook.Constants.Role;
import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.Chapter.Repo.ChapterRepository;
import Project.OpenBook.Domain.StudyProgress.ChapterSection.Repository.ChapterSectionRepository;
import Project.OpenBook.Domain.Customer.Repository.CustomerRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import javax.transaction.Transactional;
import java.util.Arrays;
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

    @Nested
    @DisplayName("queryMaxChapterNum() 메서드는")
    public class queryMaxChapterNumTest{

        @Test
        @DisplayName("존재하는 모든 단원번호중 제일 높은 번호를 Optional로 감싸서 리턴한다.")
        public void returnMaxChapterNumber() {
            //given
            Chapter ch1 = new Chapter("ch1", 1);
            Chapter ch2 = new Chapter("ch2", 123);
            Chapter ch3 = new Chapter("ch3", 43);
            chapterRepository.saveAll(Arrays.asList(ch1, ch2, ch3));

            //when
            Optional<Integer> numOptional = chapterRepository.queryMaxChapterNum();

            //then
            assertThat(numOptional.get()).isEqualTo(123);

            chapterRepository.deleteAllInBatch();
        }

        @Test
        @DisplayName("chapter가 존재하지 않는 경우 null을 리턴한다")
        public void returnNULL() {
            //given

            //when
            Optional<Integer> numOptional = chapterRepository.queryMaxChapterNum();

            //then
            assertThat(numOptional.isEmpty()).isTrue();
        }
    }
}
