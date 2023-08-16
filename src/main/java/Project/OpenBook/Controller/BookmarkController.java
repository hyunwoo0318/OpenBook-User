package Project.OpenBook.Controller;

import Project.OpenBook.Domain.Bookmark;
import Project.OpenBook.Domain.Customer;
import Project.OpenBook.Dto.BookmarkDto;
import Project.OpenBook.Service.BookmarkService;
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
@RequestMapping("/bookmarks")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @ApiOperation("해당 토픽을 북마크 추가")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "북마크 추가 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원정보나 topicTitle 입력")
    })
    @PostMapping
    public ResponseEntity<Void> addBookmark(@AuthenticationPrincipal Customer customer,
                                      @RequestBody String topicTitle) {
        Bookmark bookmark = bookmarkService.addBookmark(customer, topicTitle);

        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }

    @ApiOperation("해당 토픽에 대한 북마크 제거")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "북마크 제거 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원정보나 topicTitle 입력")
    })
    @DeleteMapping
    public ResponseEntity<Void> deleteBookmark(@AuthenticationPrincipal Customer customer,
                                         @RequestBody String topicTitle) {
        bookmarkService.deleteBookmark(customer, topicTitle);

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

}
