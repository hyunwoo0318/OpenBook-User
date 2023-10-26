package Project.OpenBook.Domain.StudyHistory.Controller;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.StudyHistory.Service.Dto.WrongCountAddDto;
import Project.OpenBook.Domain.StudyHistory.Service.StudyHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StudyHistoryController {

    private final StudyHistoryService studyHistoryService;

    //TODO
//    @Operation(summary = "연표 문제 풀이 정보 입력 - 오답")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "201", description = "단원 학습 정보 입력 성공"),
//            @ApiResponse(responseCode = "400", description = "잘못된 입력"),
//            @ApiResponse(responseCode = "404", description = "존재하지 않는 단원번호 혹은 회원아이디 입력")
//    })
//    @PatchMapping("/timeline/wrong-count")
//    public ResponseEntity<Void> addChapterProgressWrongCount(@Parameter(hidden = true) @AuthenticationPrincipal(errorOnInvalidType = true) Customer customer,
//                                                             @Validated @RequestBody ChapterProgressAddDto chapterProgressAddDto) {
//        studyProgressService.addChapterProgressWrongCount(customer, chapterProgressAddDto);
//        return new ResponseEntity<Void>(HttpStatus.OK);
//    }
//
//
    @Operation(summary = "키워드별 오답 횟수 저장")
    @PatchMapping("/keyword/wrong-count")
    public ResponseEntity<Void> saveKeywordWrongCount(@Parameter(hidden = true) @AuthenticationPrincipal(errorOnInvalidType = true) Customer customer,
                                                      @Validated @RequestBody List<WrongCountAddDto> dtoList) {
        studyHistoryService.saveKeywordWrongCount(customer, dtoList);
        return new ResponseEntity<Void> (HttpStatus.OK);
    }


}
