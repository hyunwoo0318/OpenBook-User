package Project.OpenBook.Domain.ExamQuestion.Service.dto;

import Project.OpenBook.Domain.Question.Dto.QuestionChoiceDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExamQuestionDto {

    private Long id;
    private Boolean savedAnswerNote;
    private Integer number;
    private String description;
    private List<ExamQuestionCommentDto> descriptionCommentList;
    private Integer answer;
    private String choiceType;
    private Integer score;
    private List<QuestionChoiceDto> choiceList;
    private Integer checkedChoiceKey;

    public ExamQuestionDto setByCustomerRecord(Boolean savedAnswerNote, Integer score,
        Integer checkedChoiceKey) {
        this.savedAnswerNote = savedAnswerNote;
        this.score = score;
        this.checkedChoiceKey = checkedChoiceKey;
        return this;
    }
}
