package Project.OpenBook.Domain.Search.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChapterSearchResultDto {
    private Integer chapterNumber;
    private String chapterTitle;
}
