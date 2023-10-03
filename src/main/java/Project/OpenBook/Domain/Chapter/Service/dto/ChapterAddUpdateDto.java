package Project.OpenBook.Domain.Chapter.Service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChapterAddUpdateDto {

    @NotBlank(message = "제목을 입력해주세요.")
    private String title;
    @Min(value = 1, message = "단원번호를 입력해주세요.")
    private int number;
    private String dateComment;
}
