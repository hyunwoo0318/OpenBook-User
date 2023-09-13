package Project.OpenBook.Domain.Choice.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChoiceUpdateDto {

//    @NotBlank(message = "토픽 제목을 입력해주세요.")
//    private String topic;

    @NotBlank(message = "선지 내용을 입력해주세요.")
    private String content;
}
