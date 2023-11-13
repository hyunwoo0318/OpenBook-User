package Project.OpenBook.Domain.Round.Service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RoundDto {

    @Min(value = 1, message = "회차 번호를 입력해주세요.")
    private Integer number;

    @Min(value = 1, message = "회차 년도를 입력해주세요.")
    private Integer date;
}
