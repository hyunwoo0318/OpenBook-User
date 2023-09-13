//package Project.OpenBook.Domain.AnswerNote.Controller;
//
//import Project.OpenBook.Domain.AnswerNote.Domain.AnswerNote;
//import Project.OpenBook.Domain.AnswerNote.Dto.AnswerNoteDto;
//import io.swagger.annotations.ApiOperation;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.responses.ApiResponses;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/answer-notes")
//public class AnswerNoteController {
//    private final AnswerNoteService answerNoteService;
//
//    @ApiOperation("해당 토픽을 오답노트에 추가")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "201", description = "오답노트 추가 성공"),
//            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원정보나 문제 아이디 입력")
//    })
//    @PostMapping
//    public ResponseEntity addAnswerNote(@Validated @RequestBody AnswerNoteDto answerNoteDto) {
//        AnswerNote answerNote = answerNoteService.addAnswerNote(answerNoteDto);
//
//        return new ResponseEntity(HttpStatus.CREATED);
//    }
//
//    @ApiOperation("해당 토픽에 대한 오답노트 제거")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "오답노트 제거 성공"),
//            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원정보나 문제 아이디 입력")
//    })
//    @DeleteMapping
//    public ResponseEntity deleteAnswerNote(@Validated @RequestBody AnswerNoteDto answerNoteDto) {
//        answerNoteService.deleteAnswerNote(answerNoteDto);
//        return new ResponseEntity(HttpStatus.OK);
//    }
//}
//
//
