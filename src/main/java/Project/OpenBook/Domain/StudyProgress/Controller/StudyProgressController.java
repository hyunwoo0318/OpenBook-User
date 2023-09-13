package Project.OpenBook.Domain.StudyProgress.Controller;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.StudyProgress.Dto.*;
import Project.OpenBook.Domain.StudyProgress.Service.StudyProgressService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StudyProgressController {

    private final StudyProgressService studyProgressService;

    @ApiOperation("연표 문제 풀이 정보 입력 - 오답")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "단원 학습 정보 입력 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 단원번호 혹은 회원아이디 입력")
    })
    @PatchMapping("/study-progress/chapter/wrong-count")
    public ResponseEntity<Void> addChapterProgressWrongCount(@AuthenticationPrincipal Customer customer,
                                                             @Validated @RequestBody ChapterProgressAddDto chapterProgressAddDto) {
        studyProgressService.addChapterProgressWrongCount(customer, chapterProgressAddDto);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @ApiOperation("단원/주제 학습 정보 입력 - progress 갱신")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "단원 학습 정보 입력 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 단원번호 혹은 회원아이디 입력")
    })
    @PatchMapping("/study-progress/chapter/progress")
    public ResponseEntity<Void> addChapterProgressProgressUpdate(@AuthenticationPrincipal Customer customer,
                                                                 @Validated @RequestBody ProgressDto progressDto) {
        studyProgressService.updateStudyProgress(customer, progressDto);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @ApiOperation("주제 학습 정보 입력")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "주제 학습 정보 입력 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 주제 제목 혹은 회원아이디 입력")
    })
    @PatchMapping("/study-progress/topic/wrong-count")
    public ResponseEntity<Void> addTopicProgress(@AuthenticationPrincipal Customer customer,
                                                 @Validated @RequestBody List<TopicProgressAddDto> topicProgressAddDtoList) {
        studyProgressService.addTopicProgressWrongCount(customer, topicProgressAddDtoList);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @ApiOperation("전체 진도율 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적인 조회")
    })
    @GetMapping("/total-progress")
    public ResponseEntity<TotalProgressDto> queryTotalProgress(@AuthenticationPrincipal Customer customer) {
        TotalProgressDto dto = studyProgressService.queryTotalProgress(customer);
        return new ResponseEntity<TotalProgressDto>(dto, HttpStatus.OK);
    }
}
