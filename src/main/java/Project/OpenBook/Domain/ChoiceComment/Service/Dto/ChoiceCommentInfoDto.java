package Project.OpenBook.Domain.ChoiceComment.Service.Dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class ChoiceCommentInfoDto {
    private Integer chapterNumber;
    private String topicTitle;

    private String name;
    private Long id;
    public ChoiceCommentInfoDto(Integer chapterNumber, String topicTitle,  String name, Long id) {
        this.chapterNumber = chapterNumber;
        this.topicTitle = topicTitle;
        this.name = name;
        this.id = id;
    }
}
