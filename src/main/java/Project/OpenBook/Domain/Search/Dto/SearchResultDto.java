package Project.OpenBook.Domain.Search.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SearchResultDto {
    private List<ChapterSearchResultDto> chapterList = new ArrayList<>();
    private List<TopicSearchResultDto> topicList = new ArrayList<>();
    private List<KeywordSearchResultDto> keywordList = new ArrayList<>();
}
