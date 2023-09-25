package Project.OpenBook.Domain.Era;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EraDto {

    @NotBlank(message = "시대 이름을 입력해주세요.")
    private String name;
}
