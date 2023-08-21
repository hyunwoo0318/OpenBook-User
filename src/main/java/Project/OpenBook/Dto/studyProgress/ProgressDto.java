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

    @NotBlank(message = "content를 입력해주세요.")
    private String content;

    @NotBlank(message = "title를 입력해주세요.")
    private String title;

    @NotBlank(message = "state를 입력해주세요.")
    private String state;
}
