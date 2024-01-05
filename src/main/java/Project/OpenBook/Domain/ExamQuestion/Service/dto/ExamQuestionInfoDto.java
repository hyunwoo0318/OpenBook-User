package Project.OpenBook.Domain.ExamQuestion.Service.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExamQuestionInfoDto {

    @Min(value = 1, message = "1이상의 문제 번호를 입력해주세요.")
    private Integer number;
    @Min(value = 1, message = "1이상의 정답 선지 번호를 입력해주세요.")
    private Integer answer;
    @NotBlank(message = "choiceType을 입력해주세요.")
    private String choiceType;
    private Integer score;
}
