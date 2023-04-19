package Project.OpenBook.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChoiceAddDto {

    @NotBlank(message = "토픽제목을 입력해주세요.")
    private String topicTitle;

    @NotEmpty(message = "하나이상의 선지를 입력해주세요.")
    private String[] choiceArr;
}
