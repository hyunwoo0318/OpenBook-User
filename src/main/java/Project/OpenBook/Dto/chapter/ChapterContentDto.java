package Project.OpenBook.Dto.chapter;

import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChapterContentDto {
    private String title;
    private Integer number;
    private Integer startDate;
    private Integer endDate;
    private Long topicCount;
}
