package Project.OpenBook.Domain.ChoiceComment.Controller;

import Project.OpenBook.Domain.Choice.Service.ChoiceService;
import Project.OpenBook.Domain.ChoiceComment.Service.Dto.ChoiceCommentAddUpdateDto;
import Project.OpenBook.Domain.ChoiceComment.Service.Dto.ChoiceCommentQueryDto;
import Project.OpenBook.Domain.ChoiceComment.Service.Dto.ChoiceInfoDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChoiceCommentController {

    private final ChoiceService choiceService;

    @Operation(summary = "문제의 선지 전체 조회")
    @GetMapping("/admin/rounds/{roundNumber}/questions/{questionNumber}/choices")
    public ResponseEntity queryQuestionChoices(@PathVariable("roundNumber") Integer roundNumber,
                                               @PathVariable("questionNumber") Integer questionNumber){
        List<ChoiceCommentQueryDto> dtoList
                = choiceService.queryQuestionChoices(roundNumber, questionNumber);
        return new ResponseEntity(dtoList, HttpStatus.OK);
    }

    @Operation(summary = "문제 선지 정보 입력")
    @PostMapping("/admin/rounds/{roundNumber}/questions/{questionNumber}/choice-info")
    public ResponseEntity insertChoiceInfo(@PathVariable("roundNumber") Integer roundNumber,
                                           @PathVariable("questionNumber") Integer questionNumber,
                                           @Validated @RequestBody ChoiceInfoDto dto) throws IOException {
        choiceService.createChoice(roundNumber, questionNumber, dto);
        return new ResponseEntity(HttpStatus.OK);
    }

    @Operation(summary = "문제 선지 정보 수정")
    @PatchMapping("/admin/choices/{choiceId}/choice-info")
    public ResponseEntity insertChoiceInfo(@PathVariable("choiceId")Long choiceId,
                                           @Validated @RequestBody ChoiceInfoDto dto) throws IOException {
        choiceService.updateChoice(choiceId, dto);
        return new ResponseEntity(HttpStatus.OK);
    }

    @Operation(summary = "선지 키워드/문장 추가")
    @PostMapping("/admin/choices/{choiceId}/choice-comment")
    public ResponseEntity insertChoiceKeyword(@PathVariable("choiceId")Long choiceId,
                                              @Validated @RequestBody ChoiceCommentAddUpdateDto dto) {
        choiceService.insertChoiceKeyword(choiceId, dto);
        return new ResponseEntity(HttpStatus.OK);
    }

    @Operation(summary = "선지 키워드/문장 삭제")
    @DeleteMapping("/admin/choices/{choiceId}/choice-comment")
    public ResponseEntity deleteChoiceKeyword(@PathVariable("choiceId")Long choiceId,
                                              @Validated @RequestBody ChoiceCommentAddUpdateDto dto) {
        choiceService.deleteChoiceKeyword(choiceId, dto);
        return new ResponseEntity(HttpStatus.OK);
    }



}
