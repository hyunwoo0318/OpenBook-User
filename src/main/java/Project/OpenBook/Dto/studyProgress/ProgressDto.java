package Project.OpenBook.Dto.studyProgress;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProgressDto {

    @NotNull(message = "단원 번호를 입력해주세요.")
    private Integer number;

    @NotBlank(message = "progress를 입력해주세요.")
    private String progress;
}
