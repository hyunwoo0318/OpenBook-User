package Project.OpenBook.Domain.StudyProgress.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChapterProgressAddDto {

    @NotNull(message = "단원 번호를 입력해주세요.")
    private Integer number;

    private Integer count;
}
