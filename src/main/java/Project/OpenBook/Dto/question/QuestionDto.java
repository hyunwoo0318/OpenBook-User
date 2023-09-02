package Project.OpenBook.Dto.question;

import Project.OpenBook.Dto.keyword.KeywordNameCommentDto;
import Project.OpenBook.Dto.question.QuestionChoiceDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class QuestionDto {

    private String answer;

    /**
     * TODO : questionType 추가
     */
    private String questionType;

    /**
     * 문제
     */
    private String questionSentence;

    private String description;

    private String descriptionSentence;

    private List<KeywordNameCommentDto> descriptionKeyword;

    private List<QuestionChoiceDto> choiceList;

    @Builder
    public QuestionDto(String questionType, String answer, String questionSentence, String description, String descriptionSentence, List<KeywordNameCommentDto> descriptionKeyword, List<QuestionChoiceDto> choiceList) {
        this.questionType  = questionType;
        this.answer = answer;
        this.questionSentence = questionSentence;
        this.description = description;
        this.descriptionSentence = descriptionSentence;
        this.descriptionKeyword = descriptionKeyword;
        this.choiceList = choiceList;
    }
}
