package Project.OpenBook.Repository;

import Project.OpenBook.Config.TestQueryDslConfig;
import Project.OpenBook.Domain.Category;
import Project.OpenBook.Repository.category.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import javax.persistence.EntityManager;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestQueryDslConfig.class)
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("CategoryRepository class")
public class CategoryRepositoryTest {

    @Autowired
    EntityManager entityManager;
    @Autowired
    CategoryRepository categoryRepository;

    @Nested
    @DisplayName("findCategoryByName() 메서드는")
    public class findCategoryByNameTest{

        @Nested
        @DisplayName("해당 이름을 가진 카테고리가 존재하면")
        public class existCategory{

            @Test
            @DisplayName("해당 카테고리를 Optional로 감싸서 리턴한다.")
            public void returnCategoryOptional(){
                //given
                String categoryName = "categoryName!";
                Category c1 = new Category(categoryName);
                categoryRepository.save(c1);

                //when
                Optional<Category> categoryOptional = categoryRepository.findCategoryByName(categoryName);

                //then
                assertThat(categoryOptional.get()).isEqualTo(c1);

                categoryRepository.deleteAllInBatch();
            }
        }

        @Nested
        @DisplayName("해당 이름을 가진 카테고리가 존재하지 않는다면")
        public class notExistCategory{

            @Test
            @DisplayName("null값을 Optional로 감싸서 리턴한다.")
            public void returnNullOptional(){
                //given

                //when
                Optional<Category> categoryOptional = categoryRepository.findCategoryByName("c1");

                //then
                assertThat(categoryOptional.isEmpty()).isTrue();

            }
        }
    }
}
