package Project.OpenBook.Dto.chapter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChapterInfoDto {

    @NotBlank(message = "단원 설명을 입력해주세요.")
    private String content;
}
