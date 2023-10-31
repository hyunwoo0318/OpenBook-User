package Project.OpenBook.Domain.Bookmark.Controller;

import Project.OpenBook.Domain.Bookmark.Dto.BookmarkDto;
import Project.OpenBook.Domain.Bookmark.Service.BookmarkService;
import Project.OpenBook.Domain.Customer.Domain.Customer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookmarks")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @Operation(summary = "해당 토픽을 북마크 추가")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "북마크 추가 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원정보나 topicTitle 입력")
    })
    @PostMapping
    public ResponseEntity<Void> addBookmark(@Parameter(hidden = true) @AuthenticationPrincipal(errorOnInvalidType = true) Customer customer,
                                            @RequestBody BookmarkDto dto) {
        bookmarkService.addBookmark(customer, dto);

        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }

    @Operation(summary = "해당 토픽에 대한 북마크 제거")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "북마크 제거 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원정보나 topicTitle 입력")
    })
    @DeleteMapping
    public ResponseEntity<Void> deleteBookmark(@Parameter(hidden = true) @AuthenticationPrincipal(errorOnInvalidType = true) Customer customer,
                                         @RequestBody BookmarkDto dto) {
        bookmarkService.deleteBookmark(customer, dto);

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

}
