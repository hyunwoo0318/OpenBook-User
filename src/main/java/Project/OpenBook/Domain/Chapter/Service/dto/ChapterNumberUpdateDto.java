package Project.OpenBook.Domain.Chapter.Service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChapterNumberUpdateDto {

    @Min(value = 1, message = "단원 번호를 입력해주세요.")
    private Integer number;

    @Min(value = 1, message = "단원 ID를 입력해주세요.")
    private Long id;
}
