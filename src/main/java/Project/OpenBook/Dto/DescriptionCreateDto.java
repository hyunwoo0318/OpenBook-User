package Project.OpenBook.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DescriptionCreateDto {

    @NotBlank(message = "토픽 제목을 입력해주세요.")
    private String topicTitle;

    @NotBlank(message = "보기 내용을 입력해주세요.")
    private String content;
}
