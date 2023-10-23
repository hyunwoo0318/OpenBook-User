package Project.OpenBook.Domain.Search;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.NamedStoredProcedureQueries;

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
