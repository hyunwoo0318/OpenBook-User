package Project.OpenBook.Domain.StudyHistory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WrongCountAddDto {
    private Long id;
    private Integer wrongCount;
    private Integer answerCount;
}
