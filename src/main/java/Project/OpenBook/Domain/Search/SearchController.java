package Project.OpenBook.Domain.Search;

import Project.OpenBook.Domain.Search.Dto.SearchResultDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @Operation(summary = "검색 기능")
    @GetMapping("/search")
    public ResponseEntity<SearchResultDto> searchByInput(@RequestParam("searchKey") String input) {
        SearchResultDto dto = searchService.searchByInput(input);
        return new ResponseEntity<SearchResultDto>(dto, HttpStatus.OK);
    }
}
