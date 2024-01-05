package Project.OpenBook.Domain.StudyHistory.Controller;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.StudyHistory.Service.Dto.ExamQuestionRecordDto;
import Project.OpenBook.Domain.StudyHistory.Service.Dto.ExamQuestionScoreDto;
import Project.OpenBook.Domain.StudyHistory.Service.Dto.QuestionCategoryScoreAscentDto;
import Project.OpenBook.Domain.StudyHistory.Service.Dto.WrongCountAddDto;
import Project.OpenBook.Domain.StudyHistory.Service.StudyHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StudyHistoryController {

    private final StudyHistoryService studyHistoryService;

    @Operation(summary = "연표 문제 풀이 정보 입력 - 오답")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "단원 학습 정보 입력 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 입력"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 단원번호 혹은 회원아이디 입력")
    })
    @PatchMapping("/timeline/wrong-count")
    public ResponseEntity<Void> saveTimelineWrongCount(
        @Parameter(hidden = true) @AuthenticationPrincipal(errorOnInvalidType = true) Customer customer,
        @Validated @RequestBody WrongCountAddDto dto) {
        studyHistoryService.saveTimelineWrongCount(customer, dto);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }


    @Operation(summary = "키워드별 오답 횟수 저장")
    @PatchMapping("/keyword/wrong-count")
    public ResponseEntity<List<QuestionCategoryScoreAscentDto>> saveKeywordWrongCount(
        @Parameter(hidden = true) @AuthenticationPrincipal(errorOnInvalidType = true) Customer customer,
        @Validated @RequestBody List<WrongCountAddDto> dtoList) {
        List<QuestionCategoryScoreAscentDto> returnDtoList = studyHistoryService.saveKeywordWrongCount(
            customer, dtoList);
        return new ResponseEntity<List<QuestionCategoryScoreAscentDto>>(returnDtoList,
            HttpStatus.OK);
    }

    @Operation(summary = "모의고사 회차별 오답 횟수 저장")
    @PatchMapping("/round/wrong-count")
    public ResponseEntity<Void> saveRoundWrongCount(
        @Parameter(hidden = true) @AuthenticationPrincipal(errorOnInvalidType = true) Customer customer,
        @Validated @RequestBody ExamQuestionScoreDto dto) {
        studyHistoryService.saveRoundWrongCount(customer, dto);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @Operation(summary = "모의고사 문제별 정보 저장")
    @PatchMapping("/questions/record")
    public ResponseEntity<Void> saveQuestionWrongCount(
        @Parameter(hidden = true) @AuthenticationPrincipal(errorOnInvalidType = true) Customer customer,
        @Validated @RequestBody List<ExamQuestionRecordDto> dtoList) {
        studyHistoryService.saveQuestionWrongCount(customer, dtoList);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }


}
