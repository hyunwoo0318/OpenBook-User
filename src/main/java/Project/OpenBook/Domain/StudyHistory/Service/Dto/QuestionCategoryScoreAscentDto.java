package Project.OpenBook.Domain.StudyHistory.Service.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionCategoryScoreAscentDto {
    private String questionCategoryName;
    private Double prevScore;
    private Double nextScore;
}
