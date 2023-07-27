package Project.OpenBook.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TopicProgressAddDto {
    @NotNull(message = "회원아이디를 입력해주세요.")
    private Long customerId;

    @NotBlank(message = "토픽 제목을 입력해주세요.")
    private String topicTitle;

    private Integer count;

}