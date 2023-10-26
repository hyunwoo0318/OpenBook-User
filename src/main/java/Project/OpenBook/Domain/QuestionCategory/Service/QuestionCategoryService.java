package Project.OpenBook.Domain.QuestionCategory.Service;

import Project.OpenBook.Domain.Category.Domain.Category;
import Project.OpenBook.Domain.Category.Repository.CategoryRepository;
import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.Era.Era;
import Project.OpenBook.Domain.Era.EraRepository;
import Project.OpenBook.Domain.QuestionCategory.Domain.QuestionCategory;
import Project.OpenBook.Domain.QuestionCategory.Service.Dto.QuestionCategoryAddUpdateDto;
import Project.OpenBook.Domain.QuestionCategory.Service.Dto.QuestionCategoryQueryAdminDto;
import Project.OpenBook.Domain.QuestionCategory.Service.Dto.QuestionCategoryQueryCustomerDto;
import Project.OpenBook.Domain.QuestionCategory.Repo.QuestionCategoryRepository;
import Project.OpenBook.Domain.StudyHistory.QuestionCategoryLearningRecord.QuestionCategoryLearningRecordRepository;
import Project.OpenBook.Handler.Exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static Project.OpenBook.Constants.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class QuestionCategoryService {
    private final QuestionCategoryRepository questionCategoryRepository;
    private final QuestionCategoryLearningRecordRepository questionCategoryLearningRecordRepository;
    private final EraRepository eraRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<QuestionCategoryQueryAdminDto> queryQuestionCategoryAdmin() {
        return questionCategoryRepository.queryQuestionCategoriesForAdmin().stream()
                .map(QuestionCategoryQueryAdminDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<QuestionCategoryQueryCustomerDto> queryQuestionCategoryCustomer(Customer customer) {
        return questionCategoryLearningRecordRepository.queryQuestionRecords(customer).stream()
                .map(QuestionCategoryQueryCustomerDto::new)
                .sorted(Comparator.comparing(QuestionCategoryQueryCustomerDto::getNumber))
                .collect(Collectors.toList());
    }

    @Transactional
    public void addQuestionCategory(QuestionCategoryAddUpdateDto dto) {
        String title = dto.getTitle();
        String categoryName = dto.getCategory();
        String eraName = dto.getEra();

        Category category = categoryRepository.findCategoryByName(categoryName).orElseThrow(() -> {
            throw new CustomException(CATEGORY_NOT_FOUND);
        });

        Era era = eraRepository.findByName(eraName).orElseThrow(() -> {
            throw new CustomException(ERA_NOT_FOUND);
        });

        QuestionCategory questionCategory = new QuestionCategory(title, category, era);
        questionCategoryRepository.save(questionCategory);
    }

    @Transactional
    public void updateQuestionCategory(Long questionCategoryId, QuestionCategoryAddUpdateDto dto) {
        QuestionCategory questionCategory = questionCategoryRepository.findById(questionCategoryId).orElseThrow(() -> {
            throw new CustomException(QUESTION_CATEGORY_NOT_FOUND);
        });

        String title = dto.getTitle();
        String categoryName = dto.getCategory();
        String eraName = dto.getEra();

        Category category = categoryRepository.findCategoryByName(categoryName).orElseThrow(() -> {
            throw new CustomException(CATEGORY_NOT_FOUND);
        });

        Era era = eraRepository.findByName(eraName).orElseThrow(() -> {
            throw new CustomException(ERA_NOT_FOUND);
        });

        questionCategory.updateQuestionCategory(title, category, era);
    }

    @Transactional
    public void deleteQuestionCategory(Long questionCategoryId) {
        QuestionCategory questionCategory = questionCategoryRepository.findById(questionCategoryId).orElseThrow(() -> {
            throw new CustomException(QUESTION_CATEGORY_NOT_FOUND);
        });

        questionCategoryRepository.delete(questionCategory);
    }


}
