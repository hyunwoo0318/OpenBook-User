package Project.OpenBook.Domain.Question.Dto;

import Project.OpenBook.Domain.ExamQuestion.Service.dto.ExamQuestionCommentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionChoiceDto {

    private String choice;

    private Integer number;

    private List<ExamQuestionCommentDto> commentList;

}
