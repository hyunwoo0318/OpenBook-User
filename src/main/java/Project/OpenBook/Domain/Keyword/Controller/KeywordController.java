package Project.OpenBook.Domain.Keyword.Controller;

import Project.OpenBook.Domain.Keyword.Service.KeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class KeywordController {

    private final KeywordService keywordService;

//    @Operation(summary = "키워드 전체 조회")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "키워드 전체 조회 성공"),
//    })
//    @GetMapping("/admin/keywords")
//    public ResponseEntity<List<KeywordWithTopicDto>> queryTotalKeywords() {
//        List<KeywordWithTopicDto> dtoList
//                = keywordService.queryTotalKeywords();
//        return new ResponseEntity<>(dtoList, HttpStatus.OK);
//    }




}

