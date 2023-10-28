package Project.OpenBook.Domain.JJH.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TimelineJJHAdminQueryDto {
    private String title;
    private String era;
    private Integer startDate;
    private Integer endDate;
    private Integer jjhNumber;
    private Long id;
}
