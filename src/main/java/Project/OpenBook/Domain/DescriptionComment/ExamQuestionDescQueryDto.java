package Project.OpenBook.Domain.DescriptionComment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExamQuestionDescQueryDto {
    private Integer chapterNumber;
    private String topicTitle;
    private String type;
    private String name;
    private Long id;
}
