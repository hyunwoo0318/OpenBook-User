package Project.OpenBook.Domain.QuestionComment.Service.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChoiceCommentInfoDto {

    private Integer chapterNumber;
    private String topicTitle;

    private String name;
    private Long id;

    public ChoiceCommentInfoDto(Integer chapterNumber, String topicTitle, String name, Long id) {
        this.chapterNumber = chapterNumber;
        this.topicTitle = topicTitle;
        this.name = name;
        this.id = id;
    }
}
