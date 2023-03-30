package Project.OpenBook.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Schema(name = "chapterDto",description = "단원 생성/수정에 사용되는 DTO")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChapterDto {

    @NotBlank(message = "단원제목을 입력해주세요.")
    private String title;

    @Min(value = 1, message = "단원 번호를 입력해주세요.")
    private int number;
}
