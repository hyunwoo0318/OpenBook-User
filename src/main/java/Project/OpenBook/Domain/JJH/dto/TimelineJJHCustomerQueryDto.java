package Project.OpenBook.Domain.JJH.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TimelineJJHCustomerQueryDto {
    private String era;
    private Integer startDate;
    private Integer endDate;
    private String state;
    private Integer jjhNumber;
    private Long id;
    private String title;
}
