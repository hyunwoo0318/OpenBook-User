package Project.OpenBook.Domain.ExamQuestion.Controller;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.ExamQuestion.Service.ExamQuestionService;
import Project.OpenBook.Domain.ExamQuestion.Service.dto.ExamQuestionDto;
import Project.OpenBook.Domain.ExamQuestion.Service.dto.ExamQuestionInfoDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ExamQuestionController {

    private final ExamQuestionService examQuestionService;

    @Operation(summary = "특정 회차의 모든 모의고사 문제 조회")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 회차 번호입력")
    })
    @GetMapping("/rounds/{roundNumber}/questions")
    public ResponseEntity<List<ExamQuestionDto>> getRoundQuestions(
        @Parameter(hidden = true) @AuthenticationPrincipal(errorOnInvalidType = true) Customer customer,
        @PathVariable("roundNumber") Integer roundNumber) throws JsonProcessingException {
        List<ExamQuestionDto> dtoList = examQuestionService.getRoundQuestions(customer,
            roundNumber);
        return new ResponseEntity<List<ExamQuestionDto>>(dtoList, HttpStatus.OK);
    }

    @Operation(summary = "특정 회차 문제 풀이 기록 초기화")
    @PatchMapping("/rounds/{roundNumber}/clear")
    public ResponseEntity<Void> clearRoundQuestionRecord(
        @Parameter(hidden = true) @AuthenticationPrincipal(errorOnInvalidType = true) Customer customer,
        @PathVariable("roundNumber") Integer roundNumber) {
        examQuestionService.clearRoundQuestionRecord(customer, roundNumber);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }


    @Operation(summary = "특정 문제 조회")
    @GetMapping("/questions/{id}")
    public ResponseEntity<ExamQuestionDto> getRoundQuestion(
        @Parameter(hidden = true) @AuthenticationPrincipal(errorOnInvalidType = true) Customer customer,
        @PathVariable("id") Long examQuestionId) {
        ExamQuestionDto questionDto = examQuestionService.getQuestion(customer, examQuestionId);
        return new ResponseEntity<ExamQuestionDto>(questionDto, HttpStatus.OK);
    }

    @Operation(summary = "모의고사 문제 정보 조회")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 회차 번호나 문제 번호 입력")
    })
    @GetMapping("/rounds/{roundNumber}/questions/{questionNumber}/info")
    public ResponseEntity<ExamQuestionInfoDto> getExamQuestionInfo(
        @PathVariable("roundNumber") Integer roundNumber,
        @PathVariable("questionNumber") Integer questionNumber) {
        ExamQuestionInfoDto examQuestion = examQuestionService.getExamQuestionInfo(roundNumber,
            questionNumber);
        return new ResponseEntity<ExamQuestionInfoDto>(examQuestion, HttpStatus.OK);
    }


}
