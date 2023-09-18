package Project.OpenBook.Domain.ExamQuestion.Service.dto;

import Project.OpenBook.Domain.Question.Dto.QuestionChoiceDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExamQuestionChoiceDto {

    private List<QuestionChoiceDto> choiceList;
}
