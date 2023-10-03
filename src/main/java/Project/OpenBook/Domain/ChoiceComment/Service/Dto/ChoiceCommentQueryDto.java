package Project.OpenBook.Domain.ChoiceComment.Service.Dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ChoiceCommentQueryDto {
    private String choice;
    private Integer choiceNumber;
    private Long choiceId;
    private String choiceType;
    private List<ChoiceCommentInfoDto> commentList;

    @QueryProjection
    public ChoiceCommentQueryDto(String choice, Integer choiceNumber, Long choiceId, String choiceType, List<ChoiceCommentInfoDto> commentList) {
        this.choice = choice;
        this.choiceNumber = choiceNumber;
        this.choiceId = choiceId;
        this.choiceType = choiceType;
        this.commentList = commentList;
    }
}
