package Project.OpenBook.Dto.sentence;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SentenceCreateDto {

    @NotBlank(message = "내용을 입력해주세요.")
    private String name;

    @NotBlank(message = "토픽제목을 입력해주세요.")
    private String topic;
}
