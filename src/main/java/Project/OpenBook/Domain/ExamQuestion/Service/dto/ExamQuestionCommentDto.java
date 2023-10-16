package Project.OpenBook.Domain.ExamQuestion.Service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExamQuestionCommentDto {
    private String topicDateComment;
    private String topicTitle;
    private String keywordDateComment;
    private String keywordName;
    private String keywordComment;
}
