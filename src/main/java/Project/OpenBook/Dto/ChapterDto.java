package Project.OpenBook.Dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
public class ChapterDto {

    @NotBlank(message = "단원제목을 입력해주세요.")
    private String title;

    @Min(value = 0, message = "단원 번호를 입력해주세요.")
    private int num;
}
