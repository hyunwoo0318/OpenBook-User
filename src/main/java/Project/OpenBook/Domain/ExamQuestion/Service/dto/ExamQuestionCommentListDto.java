package Project.OpenBook.Domain.ExamQuestion.Service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class ExamQuestionCommentListDto {
    private List<ExamQuestionCommentDto> commentList = new ArrayList<>();
}
