package Project.OpenBook.Domain.Round.Service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RoundAnswerNotedCountDto {
    private Integer roundNumber;
    private Integer questionCount;
}
