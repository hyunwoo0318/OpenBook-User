package Project.OpenBook.Dto.topic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TopicTempDto {
    private String title;
    private String category;
    private Integer startDate;
    private Integer endDate;
}
