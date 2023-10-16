package Project.OpenBook.Domain.Question.Dto;

import Project.OpenBook.Domain.Keyword.Dto.KeywordNameCommentDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class QuestionDto {

    private String answer;
    private String questionType;
    private String choiceType;
    private List<String> description;
    private List<ChoiceTempDto> choiceList;

    @Builder
    public QuestionDto(String answer, String questionType, String choiceType, List<String> description, List<ChoiceTempDto> choiceList) {
        this.answer = answer;
        this.questionType = questionType;
        this.choiceType = choiceType;
        this.description = description;
        this.choiceList = choiceList;
    }
}
