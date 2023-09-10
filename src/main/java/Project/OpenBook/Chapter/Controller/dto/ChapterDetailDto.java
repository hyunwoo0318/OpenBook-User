package Project.OpenBook.Chapter.Controller.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChapterDetailDto {
    private String title;
    private Integer number;
    private Integer startDate;
    private Integer endDate;
    private int topicCount;

    @Builder
    public ChapterDetailDto(String title, Integer number, Integer startDate, Integer endDate, int topicCount) {
        this.title = title;
        this.number = number;
        this.startDate = startDate;
        this.endDate = endDate;
        this.topicCount = topicCount;
    }
}
