package Project.OpenBook.Domain.ChoiceComment.Controller;

import Project.OpenBook.Domain.Choice.Service.ChoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChoiceCommentController {

    private final ChoiceService choiceService;

//    @Operation(summary = "문제의 선지 전체 조회")
//    @GetMapping("/admin/rounds/{roundNumber}/questions/{questionNumber}/choices")
//    public ResponseEntity queryQuestionChoices(@PathVariable("roundNumber") Integer roundNumber,
//                                               @PathVariable("questionNumber") Integer questionNumber){
//        List<ChoiceCommentQueryDto> dtoList
//                = choiceService.queryQuestionChoices(roundNumber, questionNumber);
//        return new ResponseEntity(dtoList, HttpStatus.OK);
//    }
}



