package Project.OpenBook.Controller;

import Project.OpenBook.Service.AnswerNoteService;
import Project.OpenBook.Service.BookmarkService;
import Project.OpenBook.Service.CustomerService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final BookmarkService bookmarkService;
    private final AnswerNoteService answerNoteService;

    @ApiOperation("특정 회원의 북마크 리스트 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적인 조회"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원 아이디 입력")
    })
    @GetMapping("customers/{customerId}/bookmarks")
    public ResponseEntity queryBookmarks(@PathVariable("customerId") Long customerId){
        List<String> titleList = bookmarkService.queryBookmarks(customerId);
        if (titleList == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity(titleList, HttpStatus.OK);
    }

    @ApiOperation("특정 회원의 오답노트 리스트 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적인 조회"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원 아이디 입력")
    })
    @GetMapping("customers/{customerId}/answer-notes")
    public ResponseEntity queryAnswerNotes(@PathVariable("customerId") Long customerId){
        List<Long> questionIdList = answerNoteService.queryAnswerNotes(customerId);
        if (questionIdList == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity(questionIdList, HttpStatus.OK);
    }

}
