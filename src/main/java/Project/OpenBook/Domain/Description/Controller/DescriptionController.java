package Project.OpenBook.Domain.Description.Controller;

import Project.OpenBook.Domain.Description.Service.DescriptionCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DescriptionController {

    private final DescriptionCommentService descriptionCommentService;

//    @Operation(summary = "문제 보기 조회")
//    @GetMapping("/rounds/{roundNumber}/questions/{questionNumber}/descriptions")
//    public ResponseEntity queryQuestionDescription(@PathVariable("roundNumber") Integer roundNumber,
//                                                   @PathVariable("questionNumber") Integer questionNumber){
//
//        DescriptionCommentDto dto = descriptionCommentService.queryQuestionDescription(roundNumber, questionNumber);
//        return new ResponseEntity(dto, HttpStatus.OK);
//    }


}
