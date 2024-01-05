package Project.OpenBook.Domain.QuestionCategory.Service;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.LearningRecord.QuestionCategoryLearningRecord.Repo.QuestionCategoryLearningRecordRepository;
import Project.OpenBook.Domain.QuestionCategory.Service.Dto.QuestionCategoryQueryCustomerDto;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuestionCategoryService {

    private final QuestionCategoryLearningRecordRepository questionCategoryLearningRecordRepository;


    @Transactional(readOnly = true)
    public List<QuestionCategoryQueryCustomerDto> queryQuestionCategoryCustomer(Customer customer) {
        return questionCategoryLearningRecordRepository.queryQuestionRecords(customer).stream()
            .map(QuestionCategoryQueryCustomerDto::new)
            .sorted(Comparator.comparing(QuestionCategoryQueryCustomerDto::getNumber))
            .collect(Collectors.toList());
    }


}
