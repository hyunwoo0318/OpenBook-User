package Project.OpenBook.Domain.AnswerNote.Controller;

import Project.OpenBook.Domain.AnswerNote.Service.Dto.AnswerNoteDto;
import Project.OpenBook.Domain.AnswerNote.Service.AnswerNoteService;
import Project.OpenBook.Domain.Customer.Domain.Customer;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/answer-notes")
public class AnswerNoteController {
    private final AnswerNoteService answerNoteService;

    @Operation(summary = "해당 토픽을 오답노트에 추가")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "오답노트 추가 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원정보나 문제 아이디 입력")
    })
    @PatchMapping
    public ResponseEntity addAnswerNote(@Parameter(hidden = true) @AuthenticationPrincipal(errorOnInvalidType = true) Customer customer,
                                        @Validated @RequestBody AnswerNoteDto answerNoteDto) {
        answerNoteService.addAnswerNote(customer, answerNoteDto);

        return new ResponseEntity(HttpStatus.CREATED);
    }

    @Operation(summary = "해당 토픽에 대한 오답노트 제거")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "오답노트 제거 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원정보나 문제 아이디 입력")
    })
    @DeleteMapping
    public ResponseEntity deleteAnswerNote(@Parameter(hidden = true) @AuthenticationPrincipal(errorOnInvalidType = true) Customer customer,
                                            @Validated @RequestBody AnswerNoteDto answerNoteDto) {
        answerNoteService.deleteAnswerNote(customer, answerNoteDto);
        return new ResponseEntity(HttpStatus.OK);
    }
}


