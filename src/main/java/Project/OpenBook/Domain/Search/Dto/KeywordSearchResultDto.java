package Project.OpenBook.Domain.Search.Dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class KeywordSearchResultDto {
    private Integer chapterNumber;
    private String chapterTitle;
    private String topicTitle;
    private String keywordName;
    private String keywordComment;
}
