package Project.OpenBook.Domain.Search.Dto;

import Project.OpenBook.Domain.Chapter.Domain.Chapter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChapterSearchResultDto {

    private Integer chapterNumber;
    private String chapterTitle;

    public ChapterSearchResultDto(Chapter chapter) {
        this.chapterNumber = chapter.getNumber();
        this.chapterTitle = chapter.getTitle();
    }
}
