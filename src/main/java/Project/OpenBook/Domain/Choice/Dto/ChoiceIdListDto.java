package Project.OpenBook.Domain.Choice.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChoiceIdListDto {
    @NotEmpty(message = "선지 id를 입력해주세요.")
    List<Long> choiceList = new ArrayList<>();
}
