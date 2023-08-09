package Project.OpenBook.Dto.question;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetTopicBySentenceQuestionDto {

    private String answer;
    private String sentence;
    private List<String> wrongAnswerList;
}
