package Project.OpenBook.Domain.Description.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExamQuestionDescQueryDto {
    private Integer chapterNumber;
    private String topicTitle;
    private String name;
    private Long id;
}
