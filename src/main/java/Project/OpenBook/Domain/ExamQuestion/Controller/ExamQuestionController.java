package Project.OpenBook.Domain.ExamQuestion.Controller;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.ExamQuestion.Service.ExamQuestionService;
import Project.OpenBook.Domain.ExamQuestion.Service.dto.ExamQuestionDto;
import Project.OpenBook.Domain.ExamQuestion.Service.dto.ExamQuestionInfoDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

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
    public ResponseEntity<List<ExamQuestionDto>> getRoundQuestions(@Parameter(hidden = true) @AuthenticationPrincipal(errorOnInvalidType = true) Customer customer,
                                                                    @PathVariable("roundNumber") Integer roundNumber) {
        List<ExamQuestionDto> dtoList = examQuestionService.getRoundQuestions(customer, roundNumber);
        return new ResponseEntity<List<ExamQuestionDto>>(dtoList, HttpStatus.OK);
    }

    @Operation(summary = "특정 회차 문제 풀이 기록 초기화")
    @PatchMapping("/rounds/{roundNumber}/clear")
    public ResponseEntity<Void> clearRoundQuestionRecord(@Parameter(hidden = true) @AuthenticationPrincipal(errorOnInvalidType = true) Customer customer,
                                                         @PathVariable("roundNumber") Integer roundNumber){
        examQuestionService.clearRoundQuestionRecord(customer, roundNumber);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

//    @Operation(summary = "특정 회차의 모든 오답노트 문제 조회")
//    @GetMapping("/questions/answer-notes")
//    public ResponseEntity<List<AnswerNotedTopicQueryDto>> getAnswerNotedQuestions(@Parameter(hidden = true) @AuthenticationPrincipal(errorOnInvalidType = true) Customer customer) {
//
//        List<AnswerNotedTopicQueryDto> dtoList = examQuestionService.getAnswerNotedQuestions(customer);
//        return new ResponseEntity<List<AnswerNotedTopicQueryDto>>(dtoList, HttpStatus.OK);
//    }


    @Operation(summary = "특정 문제 조회")
    @GetMapping("/questions/{id}")
    public ResponseEntity<ExamQuestionDto> getRoundQuestion(@Parameter(hidden = true) @AuthenticationPrincipal(errorOnInvalidType = true) Customer customer,
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
    public ResponseEntity<ExamQuestionInfoDto> getExamQuestionInfo(@PathVariable("roundNumber") Integer roundNumber,
                                                               @PathVariable("questionNumber") Integer questionNumber){
        ExamQuestionInfoDto examQuestion = examQuestionService.getExamQuestionInfo(roundNumber, questionNumber);
        return new ResponseEntity<ExamQuestionInfoDto>(examQuestion, HttpStatus.OK);
    }

//    @Operation(summary = "특정 문제의 모든 선지 조회")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "조회 성공"),
//            @ApiResponse(responseCode = "404", description = "존재하지 않는 회차 번호나 문제 번호 입력")
//    })
//    @GetMapping("/admin/rounds/{roundNumber}/questions/{questionNumber}/choices")
//    public ResponseEntity<ExamQuestionChoiceDto> getExamQuestionChoices(@PathVariable("roundNumber") Integer roundNumber,
//                                                                              @PathVariable("questionNumber") Integer questionNumber) {
//        ExamQuestionChoiceDto dto = examQuestionService.getExamQuestionChoices(roundNumber, questionNumber);
//        return new ResponseEntity<>(dto, HttpStatus.OK);
//    }


    @Operation(summary = "모의고사 문제 저장")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모의고사 문제 저장 성공"),
            @ApiResponse(responseCode = "400" , description = "잘못된 입력"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회차 번호 입력")
    })
    @PostMapping("/admin/rounds/{roundNumber}/questions")
    public ResponseEntity<Void> saveExamQuestionInfo(@PathVariable("roundNumber") Integer roundNumber,
                                                 @Validated @RequestBody ExamQuestionInfoDto examQuestionInfoDto) throws IOException {
        examQuestionService.saveExamQuestionInfo(roundNumber, examQuestionInfoDto);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
//
//    @Operation(summary = "모의고사 문제 선지 저장")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "모의고사 문제 선지 저장 성공"),
//            @ApiResponse(responseCode = "400" , description = "잘못된 입력"),
//            @ApiResponse(responseCode = "404", description = "존재하지 않는 회차 번호, 문제 번호, 토픽 제목 입력")
//    })
//    @PostMapping("/admin/rounds/{roundNumber}/questions/{questionNumber}/choices")
//    public ResponseEntity<Void> saveExamQuestionChoice(@PathVariable("roundNumber") Integer roundNumber,
//                                                       @PathVariable("questionNumber") Integer questionNumber,
//                                                       @Validated @RequestBody ChoiceAddUpdateDto choiceAddUpdateDto) throws IOException {
//
//        examQuestionService.saveExamQuestionChoice(roundNumber, questionNumber, choiceAddUpdateDto);
//        return new ResponseEntity<Void>(HttpStatus.CREATED);
//    }

    @Operation(summary = "모의고사 문제 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모의고사 문제 수정 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회차 번호나 문제 번호 입력")
    })
    @PatchMapping("/admin/rounds/{roundNumber}/questions/{questionNumber}/info")
    public ResponseEntity<Void> updateExamQuestion(@PathVariable("roundNumber") Integer roundNumber,
                                                 @PathVariable("questionNumber") Integer questionNumber,
                                                 @Validated @RequestBody ExamQuestionInfoDto examQuestionInfoDto) throws IOException {
        examQuestionService.updateExamQuestionInfo(roundNumber, questionNumber, examQuestionInfoDto);
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
