package Project.OpenBook.Dto.question;

import Project.OpenBook.Dto.Sentence.SentenceWithTopicDto;
import Project.OpenBook.Dto.keyword.KeywordWithTopicDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetKeywordWrongAnswerDto {

        private List<KeywordWithTopicDto> keywordList;
        private List<SentenceWithTopicDto> sentenceList;
}
