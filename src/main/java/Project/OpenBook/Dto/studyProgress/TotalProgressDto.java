package Project.OpenBook.Dto.studyProgress;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TotalProgressDto {
    private Integer totalProgress;
}
