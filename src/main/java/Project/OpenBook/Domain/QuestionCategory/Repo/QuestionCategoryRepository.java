package Project.OpenBook.Domain.QuestionCategory.Repo;

import Project.OpenBook.Domain.Category.Domain.Category;
import Project.OpenBook.Domain.Era.Era;
import Project.OpenBook.Domain.QuestionCategory.Domain.QuestionCategory;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionCategoryRepository extends JpaRepository<QuestionCategory, Long>,
    QuestionCategoryRepositoryCustom {

    public Optional<QuestionCategory> findByCategoryAndEra(Category category, Era era);
}
