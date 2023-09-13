package Project.OpenBook.Domain.ExamQuestion.Controller;

import Project.OpenBook.Domain.ExamQuestion.Service.dto.ExamQuestionDto;
import Project.OpenBook.Domain.ExamQuestion.Service.ExamQuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class ExamQuestionController {

    private final ExamQuestionService examQuestionService;


    @Operation(summary = "모의고사 문제 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회차 번호나 문제 번호 입력")
    })
    @GetMapping("/rounds/{roundNumber}/questions/{questionNumber}")
    public ResponseEntity<ExamQuestionDto> getExamQuestion(@PathVariable("roundNumber") Integer roundNumber,
                                                           @PathVariable("questionNumber") Integer questionNumber){
        ExamQuestionDto examQuestion = examQuestionService.getExamQuestion(roundNumber, questionNumber);
        return new ResponseEntity<ExamQuestionDto>(examQuestion, HttpStatus.OK);
    }

    @Operation(summary = "모의고사 문제 저장")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모의고사 문제 저장 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회차 번호 입력")
    })
    @PostMapping("/admin/rounds/{roundNumber}/questions")
    public ResponseEntity<Void> saveExamQuestion(@PathVariable("roundNumber") Integer roundNumber,
                                                 @Validated @RequestBody ExamQuestionDto examQuestionDto) throws IOException {
        examQuestionService.saveExamQuestion(roundNumber, examQuestionDto);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @Operation(summary = "모의고사 문제 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모의고사 문제 수정 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회차 번호나 문제 번호 입력")
    })
    @PatchMapping("/admin/rounds/{roundNumber}/questions/{questionNumber}")
    public ResponseEntity<Void> updateExamQuestion(@PathVariable("roundNumber") Integer roundNumber,
                                                 @PathVariable("questionNumber") Integer questionNumber,
                                                 @Validated @RequestBody ExamQuestionDto examQuestionDto) throws IOException {
        examQuestionService.updateExamQuestion(roundNumber, questionNumber, examQuestionDto);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @Operation(summary = "모의고사 문제 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모의고사 문제 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회차 번호 입력")
    })
    @DeleteMapping("/admin/rounds/{roundNumber}/questions/{questionNumber}")
    public ResponseEntity<Void> saveExamQuestion(@PathVariable("roundNumber") Integer roundNumber,
                                                 @PathVariable("questionNumber") Integer questionNumber){
        examQuestionService.deleteExamQuestion(roundNumber, questionNumber);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
