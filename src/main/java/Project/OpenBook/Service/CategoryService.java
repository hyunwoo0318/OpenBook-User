package Project.OpenBook.Service;

import Project.OpenBook.Domain.Category;
import Project.OpenBook.Domain.Topic;
import Project.OpenBook.Dto.ErrorDto;
import Project.OpenBook.Repository.CategoryRepository;
import Project.OpenBook.Repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final TopicRepository topicRepository;

    public List<String> queryCategories() {
        return categoryRepository.findAll().stream().map(c -> c.getName()).collect(Collectors.toList());
    }

    public Category createCategory(String categoryName, List<ErrorDto> errorDtoList) {
        Optional<Category> categoryOptional = categoryRepository.findCategoryByName(categoryName);
        //중복된 카테고리 입력 확인
        if (categoryOptional.isPresent()) {
            errorDtoList.add(new ErrorDto("name", "이미 존재하는 카테고리입니다."));
            return null;
        }

        Category category = new Category(categoryName);
        categoryRepository.save(category);
        return category;
    }

    public Category updateCategory(String prevName, String afterName, List<ErrorDto> errorDtoList) {
        Optional<Category> categoryPrevOptional = categoryRepository.findCategoryByName(prevName);
        if (categoryPrevOptional.isEmpty()) {
            return null;
        }

        Optional<Category> categoryAfterOptional = categoryRepository.findCategoryByName(afterName);
        if (categoryAfterOptional.isPresent()) {
            errorDtoList.add(new ErrorDto("name", "이미 존재하는 카테고리입니다."));
            return null;
        }

        Category category = categoryPrevOptional.get();
        category.changeName(afterName);
        return category;
    }

    public boolean deleteCategory(String categoryName) {
        Optional<Category> categoryOptional = categoryRepository.findCategoryByName(categoryName);
        if (categoryOptional.isEmpty()) {
            return false;
        }
        Category category = categoryOptional.get();
        List<Topic> topicList = topicRepository.findAllByCategory(category);
        topicList.stream().forEach(c -> c.deleteCategory());

        categoryRepository.delete(category);
        return true;
    }


}
