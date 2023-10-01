package Project.OpenBook.Domain.ChoiceComment;

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

    @Setter
    private String type;

    private String name;
    private Long id;


    public ChoiceCommentInfoDto(Integer chapterNumber, String topicTitle, String type, String name, Long id) {
        this.chapterNumber = chapterNumber;
        this.topicTitle = topicTitle;
        this.type = type;
        this.name = name;
        this.id = id;
    }


}
