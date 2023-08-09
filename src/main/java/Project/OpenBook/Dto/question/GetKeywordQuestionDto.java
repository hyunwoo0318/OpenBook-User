package Project.OpenBook.Dto.question;

import Project.OpenBook.Dto.keyword.KeywordNameCommentDto;
import Project.OpenBook.Dto.keyword.KeywordWithTopicDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetKeywordQuestionDto {

    private List<KeywordNameCommentDto> answerKeywordList;

    private List<KeywordWithTopicDto> wrongAnswerKeywordList;
}
