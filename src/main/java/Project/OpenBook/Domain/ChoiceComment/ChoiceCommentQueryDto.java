package Project.OpenBook.Domain.ChoiceComment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChoiceCommentQueryDto {
    private String choice;
    private Integer choiceNumber;
    private Long choiceId;
    private String choiceType;
    private List<ChoiceCommentInfoDto> commentList;


}
