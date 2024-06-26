package Project.OpenBook.Domain.QuestionCategory.Controller;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.QuestionCategory.Repo.QuestionCategoryRepository;
import Project.OpenBook.Domain.QuestionCategory.Service.Dto.QuestionCategoryQueryCustomerDto;
import Project.OpenBook.Domain.QuestionCategory.Service.QuestionCategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class QuestionCategoryController {

    private final QuestionCategoryService questionCategoryService;
    private final QuestionCategoryRepository questionCategoryRepository;

    @Operation(summary = "사용자 페이지에서 question-category 조회")
    @GetMapping("/question-categories")
    public ResponseEntity<List<QuestionCategoryQueryCustomerDto>> queryQuestionCategoryCustomer(
            @Parameter(hidden = true) @AuthenticationPrincipal Customer customer) {
        if (customer != null) {
            List<QuestionCategoryQueryCustomerDto> dtoList =
                    questionCategoryService.queryQuestionCategoryCustomer(customer);
            return new ResponseEntity<List<QuestionCategoryQueryCustomerDto>>(
                    dtoList, HttpStatus.OK);
        } else {
            List<QuestionCategoryQueryCustomerDto> dtoList =
                    questionCategoryRepository.findAll().stream()
                            .map(QuestionCategoryQueryCustomerDto::new)
                            .sorted(
                                    Comparator.comparing(
                                            QuestionCategoryQueryCustomerDto::getNumber))
                            .collect(Collectors.toList());
            return new ResponseEntity<List<QuestionCategoryQueryCustomerDto>>(
                    dtoList, HttpStatus.OK);
        }
    }
}
