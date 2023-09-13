package Project.OpenBook.Domain.Choice.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChoiceIdDto {
    @NotNull(message = "선지 id를 입력해주세요.")
    private Long choiceId;
}
