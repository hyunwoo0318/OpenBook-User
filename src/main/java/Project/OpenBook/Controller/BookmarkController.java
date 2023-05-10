package Project.OpenBook.Controller;

import Project.OpenBook.Domain.Bookmark;
import Project.OpenBook.Dto.BookmarkDto;
import Project.OpenBook.Service.BookmarkService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookmarks")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @ApiOperation("특정 회원의 오답노트 리스트 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적인 조회"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원 아이디 입력")
    })
    @GetMapping("/{customerId}")
    public ResponseEntity queryBookmarks(@PathVariable("customerId") Long customerId){
        List<String> titleList = bookmarkService.queryBookmarks(customerId);
        if (titleList == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity(titleList, HttpStatus.OK);
    }

    @ApiOperation("해당 토픽을 북마크 추가")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "북마크 추가 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원정보나 topicTitle 입력")
    })
    @PostMapping
    public ResponseEntity addBookmark(@Validated @RequestBody BookmarkDto bookmarkDto) {
        Bookmark bookmark = bookmarkService.addBookmark(bookmarkDto);
        if (bookmark == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity(HttpStatus.CREATED);
    }

    @ApiOperation("해당 토픽에 대한 북마크 제거")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "북마크 제거 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원정보나 topicTitle 입력")
    })
    @DeleteMapping
    public ResponseEntity deleteBookmark(@Validated @RequestBody BookmarkDto bookmarkDto) {
        boolean res = bookmarkService.deleteBookmark(bookmarkDto);
        if (res == false) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity(HttpStatus.OK);
    }

}
