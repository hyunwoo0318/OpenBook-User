package Project.OpenBook.Dto.question;

import Project.OpenBook.Dto.keyword.KeywordNameCommentDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetKeywordAnswerDto {
    private List<KeywordNameCommentDto> keywordList;
    private List<String> sentenceList;
}
