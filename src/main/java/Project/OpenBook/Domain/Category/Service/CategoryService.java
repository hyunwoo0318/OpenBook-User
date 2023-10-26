package Project.OpenBook.Domain.Category.Service;

import Project.OpenBook.Domain.Category.Domain.Category;
import Project.OpenBook.Domain.Category.Repository.CategoryRepository;
import Project.OpenBook.Domain.Category.Service.Dto.CategoryDto;
import Project.OpenBook.Domain.QuestionCategory.Domain.QuestionCategory;
import Project.OpenBook.Handler.Exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static Project.OpenBook.Constants.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;


    @Transactional(readOnly = true)
    public List<CategoryDto> queryCategories() {
        return categoryRepository.findAll().stream().map(c -> new CategoryDto(c.getName())).collect(Collectors.toList());
    }

    @Transactional
    public Category createCategory(CategoryDto categoryDto) {
        String categoryName = categoryDto.getName();
        checkDupCategoryName(categoryName);

        Category category = new Category(categoryName);
        categoryRepository.save(category);
        return category;
    }

    @Transactional
    public Category updateCategory(String prevName, String afterName) {

        Category category = checkCategory(prevName);
        if (prevName.equals(afterName)) {
            return category;
        }
        checkDupCategoryName(afterName);

        category.changeName(afterName);
        return category;
    }

    @Transactional
    public boolean deleteCategory(String categoryName) {
        Category category = checkCategory(categoryName);

        List<QuestionCategory> questionCategoryList = category.getQuestionCategoryList();
        if(!questionCategoryList.isEmpty()){
            throw new CustomException(CATEGORY_HAS_TOPIC);
        }

        categoryRepository.delete(category);
        return true;
    }


    private Category checkCategory(String categoryName) {
        return categoryRepository.findCategoryByName(categoryName).orElseThrow(() -> {
            throw new CustomException(CATEGORY_NOT_FOUND);
        });
    }

    private void checkDupCategoryName(String categoryName) {
        categoryRepository.findCategoryByName(categoryName).ifPresent(c -> {
            throw new CustomException(DUP_CATEGORY_NAME);
        });
    }


}
