package Project.OpenBook.Domain.ChoiceComment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChoiceCommentInfoDto {
    private Integer chapterNumber;
    private String topicTitle;
    private String type;
    private String name;
    private Long id;
}
