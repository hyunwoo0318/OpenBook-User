package Project.OpenBook.Controller;

import Project.OpenBook.Dto.ChapterProgressAddDto;
import Project.OpenBook.Dto.TopicProgressAddDto;
import Project.OpenBook.Service.StudyProgressService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StudyProgressController {

    private final StudyProgressService studyProgressService;

    @ApiOperation("단원 학습 정보 입력")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "단원 학습 정보 입력 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 단원번호 혹은 회원아이디 입력")
    })
    @PostMapping("/admin/progress/chapter")
    public ResponseEntity addChapterProgress(@Validated @RequestBody ChapterProgressAddDto chapterProgressAddDto) {
        studyProgressService.addChapterProgress(chapterProgressAddDto);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @ApiOperation("주제 학습 정보 입력")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "주제 학습 정보 입력 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 주제 제목 혹은 회원아이디 입력")
    })
    @PostMapping("/admin/progress/topic")
    public ResponseEntity addTopicProgress(@Validated @RequestBody TopicProgressAddDto topicProgressAddDto) {
        studyProgressService.addTopicProgress(topicProgressAddDto);
        return new ResponseEntity(HttpStatus.CREATED);
    }
}
