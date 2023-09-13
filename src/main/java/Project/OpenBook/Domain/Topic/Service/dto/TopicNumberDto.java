package Project.OpenBook.Domain.Topic.Service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TopicNumberDto {
    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    @Min(value = 1, message = "1 이상의 번호를 입력해주세요.")
    private Integer number;
}
