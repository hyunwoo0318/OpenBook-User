package Project.OpenBook.Domain.Keyword.Service.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionNumberDto {
    private Integer roundNumber;
    private Integer questionNumber;
    private String choice;
}
