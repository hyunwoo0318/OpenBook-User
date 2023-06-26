package Project.OpenBook.Dto.choice;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChoiceIdDto {
    @Min(value = 1, message = "선지 id를 입력해주세요.")
    private Long choiceId;
}
