package Project.OpenBook.Dto.chapter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChapterContentDto {
    private String title;
    private Integer number;
    private Integer startDate;
    private Integer endDate;
    private int topicCount;

    @Builder
    public ChapterContentDto(String title, Integer number, Integer startDate, Integer endDate, int topicCount) {
        this.title = title;
        this.number = number;
        this.startDate = startDate;
        this.endDate = endDate;
        this.topicCount = topicCount;
    }
}
