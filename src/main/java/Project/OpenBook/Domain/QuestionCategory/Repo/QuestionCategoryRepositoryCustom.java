package Project.OpenBook.Domain.QuestionCategory.Repo;

import Project.OpenBook.Domain.QuestionCategory.Domain.QuestionCategory;

import java.util.Optional;

public interface QuestionCategoryRepositoryCustom {

    public Optional<QuestionCategory> queryQuestionCategoriesWithTopicList(Long id);
}
