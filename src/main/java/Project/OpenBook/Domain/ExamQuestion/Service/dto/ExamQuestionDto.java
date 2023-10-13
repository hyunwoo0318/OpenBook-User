package Project.OpenBook.Domain.ExamQuestion.Service.dto;

import Project.OpenBook.Domain.Question.Dto.QuestionChoiceDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExamQuestionDto {
    private Integer number;
    private String description;
    private String descriptionComment;
    private Integer answer;
    private String choiceType;
    private Integer score;
    private List<QuestionChoiceDto> choiceList;
}
