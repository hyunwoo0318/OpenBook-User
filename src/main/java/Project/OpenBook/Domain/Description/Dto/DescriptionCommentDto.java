package Project.OpenBook.Domain.Description.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DescriptionCommentDto {
    private String description;
    private Long descriptionId;
    private List<ExamQuestionDescQueryDto> commentList;
}
