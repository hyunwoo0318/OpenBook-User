package Project.OpenBook.Domain.Question.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TimeFlowQuestionDto {
    private Integer date;
    private String comment;
    private String topicTitle;
}
