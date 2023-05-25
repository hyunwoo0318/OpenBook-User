package Project.OpenBook.Dto.customer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDetailDto {

    @NotBlank(message = "별명을 입력해주세요.")
    private String nickname;

    @Min(1)@Max(9)
    private Integer currentGrade;

    @Min(1)@Max(100)
    private Integer age;
}
