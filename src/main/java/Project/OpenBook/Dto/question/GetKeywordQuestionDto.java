package Project.OpenBook.Dto.question;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetKeywordQuestionDto {

    public GetKeywordAnswerDto answer;
    public GetKeywordWrongAnswerDto wrongAnswer;
}
