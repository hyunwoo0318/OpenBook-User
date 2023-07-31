package Project.OpenBook.Dto.question;

import Project.OpenBook.Dto.keyword.KeywordNameCommentDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetTopicQuestionDto {

    public String answer;
    public String type;
    public List<KeywordNameCommentDto> keywordList = new ArrayList<>();
    public String sentence;
    public List<String> wrongAnswerList = new ArrayList<>();

    public GetTopicQuestionDto(String answer, String type, List<KeywordNameCommentDto> keywordList, List<String> wrongAnswerList) {
        this.answer = answer;
        this.type = type;
        this.keywordList = keywordList;
        this.wrongAnswerList = wrongAnswerList;
    }

    public GetTopicQuestionDto(String answer, String type, String sentence, List<String> wrongAnswerList) {
        this.answer = answer;
        this.type = type;
        this.sentence = sentence;
        this.wrongAnswerList = wrongAnswerList;
    }
}
