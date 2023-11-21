package Project.OpenBook.Domain.QuestionCategory.Repo;

import Project.OpenBook.Domain.QuestionCategory.Domain.QuestionCategory;

import java.util.List;
import java.util.Optional;

public interface QuestionCategoryRepositoryCustom {
    public List<QuestionCategory> queryQuestionCategoriesForAdmin();

    public Optional<QuestionCategory> queryQuestionCategoriesWithTopicList(Long id);

}
