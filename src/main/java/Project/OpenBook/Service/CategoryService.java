package Project.OpenBook.Service;

import Project.OpenBook.Dto.category.CategoryDto;
import Project.OpenBook.Utils.CustomException;
import Project.OpenBook.Domain.Category;
import Project.OpenBook.Domain.Topic;
import Project.OpenBook.Repository.category.CategoryRepository;
import Project.OpenBook.Repository.topic.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

import static Project.OpenBook.Constants.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final TopicRepository topicRepository;


    public List<CategoryDto> queryCategories() {
        return categoryRepository.findAll().stream().map(c -> new CategoryDto(c.getName())).collect(Collectors.toList());
    }

    public Category createCategory(String categoryName) {
        checkDupCategoryName(categoryName);

        Category category = new Category(categoryName);
        categoryRepository.save(category);
        return category;
    }

    public Category updateCategory(String prevName, String afterName) {

        Category category = checkCategory(prevName);
        if (prevName.equals(afterName)) {
            return category;
        }
        checkDupCategoryName(afterName);

        category.changeName(afterName);
        return category;
    }

    public void deleteCategory(String categoryName) {
        Category category = checkCategory(categoryName);

        List<Topic> topicList = topicRepository.findAllByCategory(category);
        if (!topicList.isEmpty()) {
            throw new CustomException(CATEGORY_HAS_TOPIC);
        }

        categoryRepository.delete(category);
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
