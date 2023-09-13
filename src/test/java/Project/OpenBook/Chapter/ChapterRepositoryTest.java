package Project.OpenBook.Chapter;

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



}
