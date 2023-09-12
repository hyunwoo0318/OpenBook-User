package Project.OpenBook.ExamQuestion.Controller.dto;

import Project.OpenBook.Dto.question.QuestionChoiceDto;
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
    private String answer;
    private String choiceType;
    private List<QuestionChoiceDto> choiceList;
    private Integer score;
}
