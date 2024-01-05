package Project.OpenBook.Domain.Question.Dto;

import Project.OpenBook.Domain.ExamQuestion.Service.dto.ExamQuestionCommentDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionChoiceDto {

    private String choice;
    private Integer number;
    private List<ExamQuestionCommentDto> commentList;
}
