package Project.OpenBook.Domain.Description.Controller;

import Project.OpenBook.Domain.Description.Dto.DescriptionCommentAddDto;
import Project.OpenBook.Domain.Description.Dto.DescriptionUpdateDto;
import Project.OpenBook.Domain.Description.Dto.DescriptionCommentDto;
import Project.OpenBook.Domain.Description.Service.DescriptionCommentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class DescriptionController {

    private final DescriptionCommentService descriptionCommentService;

    @Operation(summary = "문제 보기 조회")
    @GetMapping("/rounds/{roundNumber}/questions/{questionNumber}/descriptions")
    public ResponseEntity queryQuestionDescription(@PathVariable("roundNumber") Integer roundNumber,
                                                   @PathVariable("questionNumber") Integer questionNumber){

        DescriptionCommentDto dto = descriptionCommentService.queryQuestionDescription(roundNumber, questionNumber);
        return new ResponseEntity(dto, HttpStatus.OK);
    }

    @Operation(summary = "보기 키워드/문장 추가")
    @PostMapping("/descriptions/{id}")
    public ResponseEntity insertDescriptionKeyword(@PathVariable("id") Long descriptionId,
                                                           @Validated @RequestBody DescriptionCommentAddDto descriptionCommentAddDto){
        descriptionCommentService.insertDescriptionKeyword(descriptionId, descriptionCommentAddDto);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @Operation(summary = "보기 키워드/문장 삭제")
    @DeleteMapping("/descriptions/{id}")
    public ResponseEntity deleteDescriptionKeyword(@PathVariable("id") Long descriptionId,
                                                           @Validated @RequestBody DescriptionCommentAddDto descriptionCommentAddDto){
        descriptionCommentService.deleteDescriptionKeyword(descriptionId, descriptionCommentAddDto);
        return new ResponseEntity(HttpStatus.OK);
    }

    @Operation(summary = "보기 이미지 수정")
    @PatchMapping("/descriptions/{id}")
    public ResponseEntity updateDescription(@PathVariable("id") Long descriptionId,
                                            @Validated @RequestBody DescriptionUpdateDto descriptionUpdateDto) throws IOException {
        descriptionCommentService.updateDescription(descriptionId, descriptionUpdateDto);
        return new ResponseEntity(HttpStatus.OK);
    }
}
