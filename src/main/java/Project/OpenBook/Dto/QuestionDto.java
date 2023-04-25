package Project.OpenBook.Dto;

import Project.OpenBook.Domain.Choice;
import Project.OpenBook.Domain.Question;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class QuestionDto {

    private Long id;
    private Long type;
    @NotBlank(message = "질문을 입력해주세요.")
    private String prompt;

    @NotBlank(message = "카테고리 이름을 입력해주세요")
    private String categoryName;

    private DescriptionContentIdDto description;
    @Size(min = 5, max = 5)
    private List<ChoiceContentIdDto>  choiceList;

    @NotNull(message = "정답 ID를 입력해주세요.")
    private Long answerChoiceId;

    @Builder
    public QuestionDto(Long id,Long type, String prompt, String categoryName, DescriptionContentIdDto description, List<ChoiceContentIdDto> choiceList, Long answerChoiceId) {
        this.id = id;
        this.type = type;
        this.prompt = prompt;
        this.categoryName = categoryName;
        this.description = description;
        this.choiceList = choiceList;
        this.answerChoiceId = answerChoiceId;
    }

    public QuestionDto(Question question,List<ChoiceContentIdDto> choiceList, DescriptionContentIdDto descriptionContentIdDto) {
        this.id = question.getId();
        this.type = question.getType();
        this.prompt = question.getPrompt();
        this.categoryName = question.getCategory().getName();
        this.choiceList = choiceList;
        this.answerChoiceId = question.getAnswerChoiceId();
        this.description = descriptionContentIdDto;
    }
}
