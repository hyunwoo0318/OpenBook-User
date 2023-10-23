package Project.OpenBook.Domain.Search.ChapterSearch;


import java.util.List;

public interface ChapterSearchRepositoryCustom {

    List<ChapterSearch> queryChapterSearchNameByInput(String input);
}
