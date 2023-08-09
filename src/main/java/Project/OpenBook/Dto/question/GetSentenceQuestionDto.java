package Project.OpenBook.Dto.question;

import Project.OpenBook.Dto.Sentence.SentenceWithTopicDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetSentenceQuestionDto {
    private List<String> answerSentenceList;
    private List<SentenceWithTopicDto> wrongAnswerSentenceList;
}
