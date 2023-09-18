package Project.OpenBook.Domain.ExamQuestion.Service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChoiceAddUpdateDto {

    @NotBlank(message = "선지를 입력해주세요.")
    private String choice;

    private String comment;

    @NotBlank(message = "선지가 속한 토픽 제목을 입력해주세요.")
    private String key;

    @NotBlank(message = "선지 타입을 입력해주세요.")
    private String choiceType;
}
