package Project.OpenBook.Domain.Search.KeywordSearch;


import java.util.List;

public interface KeywordSearchRepositoryCustom {

    List<KeywordSearch> queryKeywordSearchNameByInput(String input);
    List<KeywordSearch> queryKeywordSearchCommentByInput(String input);
}
