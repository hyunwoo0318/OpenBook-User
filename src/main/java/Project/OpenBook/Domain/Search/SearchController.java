package Project.OpenBook.Domain.Search;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/search-temp")
    public void searchByInput(@RequestParam("searchKey") String input) {
        searchService.searchByInput(input);

    }
}
