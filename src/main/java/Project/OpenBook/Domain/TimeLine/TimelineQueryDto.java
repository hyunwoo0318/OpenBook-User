package Project.OpenBook.Domain.TimeLine;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TimelineQueryDto {
    private String era;
    private Integer startDate;
    private Integer endDate;
    private Integer jjhNumber;
    private Long id;
}
