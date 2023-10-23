package Project.OpenBook.Domain.TimeLine;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TimelineAddUpdateDto {
    private String era;
    private Integer startDate;
    private Integer endDate;
}
