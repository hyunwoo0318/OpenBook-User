package Project.OpenBook.Domain.Question.Dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TimeFlowQuestionDto {

    private Integer date;
    private String comment;
    private List<String> keywordList;
}
