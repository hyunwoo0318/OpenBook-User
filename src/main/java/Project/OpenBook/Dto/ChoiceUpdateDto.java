package Project.OpenBook.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChoiceUpdateDto {

    @NotEmpty(message = "하나 이상의 선지를 입력해주세요.")
    private List<ChoiceContentIdDto> choiceList;
}
