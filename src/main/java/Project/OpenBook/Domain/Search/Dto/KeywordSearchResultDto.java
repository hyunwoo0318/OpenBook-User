package Project.OpenBook.Domain.Search.Dto;

import Project.OpenBook.Domain.Keyword.Domain.Keyword;
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

    public KeywordSearchResultDto(Keyword keyword) {
        this.chapterNumber = keyword.getTopic().getChapter().getNumber();
        this.chapterTitle = keyword.getTopic().getChapter().getTitle();
        this.topicTitle = keyword.getTopic().getTitle();
        this.keywordName = keyword.getName();
        this.keywordComment = keyword.getComment();
    }
}
