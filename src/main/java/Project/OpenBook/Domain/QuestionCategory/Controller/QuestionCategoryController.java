package Project.OpenBook.Domain.QuestionCategory.Controller;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.QuestionCategory.Service.Dto.QuestionCategoryAddUpdateDto;
import Project.OpenBook.Domain.QuestionCategory.Service.Dto.QuestionCategoryNumberUpdateDto;
import Project.OpenBook.Domain.QuestionCategory.Service.Dto.QuestionCategoryQueryAdminDto;
import Project.OpenBook.Domain.QuestionCategory.Service.Dto.QuestionCategoryQueryCustomerDto;
import Project.OpenBook.Domain.QuestionCategory.Service.QuestionCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class QuestionCategoryController {

    private final QuestionCategoryService questionCategoryService;

    @Operation(summary = "관리자 페이지에서 question-category 조회")
    @GetMapping("/admin/question-categories")
    public ResponseEntity<List<QuestionCategoryQueryAdminDto>> queryQuestionCategoryAdmin() {
        List<QuestionCategoryQueryAdminDto> dtoList =
                questionCategoryService.queryQuestionCategoryAdmin();
        return new ResponseEntity<List<QuestionCategoryQueryAdminDto>>(dtoList, HttpStatus.OK);
    }

    @Operation(summary = "사용자 페이지에서 question-category 조회")
    @GetMapping("/question-categories")
    public ResponseEntity<List<QuestionCategoryQueryCustomerDto>> queryQuestionCategoryCustomer(@Parameter(hidden = true) @AuthenticationPrincipal(errorOnInvalidType = true) Customer customer) {
        List<QuestionCategoryQueryCustomerDto> dtoList = questionCategoryService.queryQuestionCategoryCustomer(customer);
        return new ResponseEntity<List<QuestionCategoryQueryCustomerDto>>(dtoList, HttpStatus.OK);
    }

    @Operation(summary = "관리자 페이지에서 question-category 생성")
    @PostMapping("/admin/question-categories")
    public ResponseEntity<Void> addQuestionCategory(@Validated @RequestBody QuestionCategoryAddUpdateDto dto) {
        questionCategoryService.addQuestionCategory(dto);
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }

    @Operation(summary = "관리자 페이지에서 question-category 수정")
    @PatchMapping("/admin/question-categories/{questionCategoryId}")
    public ResponseEntity<Void> updateQuestionCategory(@PathVariable Long questionCategoryId,
                                                   @Validated @RequestBody QuestionCategoryAddUpdateDto dto) {
        questionCategoryService.updateQuestionCategory(questionCategoryId,dto);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @Operation(summary = "관리자 페이지에서 question-category 삭제")
    @DeleteMapping("/admin/question-categories/{questionCategoryId}")
    public ResponseEntity<Void> deleteQuestionCategory(@PathVariable Long questionCategoryId) {
        questionCategoryService.deleteQuestionCategory(questionCategoryId);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @Operation(summary = "관리자 페이지에서 question-category 순서 변경")
    @PatchMapping("/admin/question-category-numbers")
    public ResponseEntity<Void> updateQCNumber(@Validated @RequestBody List<QuestionCategoryNumberUpdateDto> dtoList) {
        questionCategoryService.updateQCNumber(dtoList);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
