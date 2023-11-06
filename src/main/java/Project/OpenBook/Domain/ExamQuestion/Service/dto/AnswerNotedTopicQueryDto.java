package Project.OpenBook.Domain.ExamQuestion.Service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AnswerNotedTopicQueryDto {
    private Integer roundNumber;
    private List<AnswerNotedQuestionInfoDto> questionList;

}
