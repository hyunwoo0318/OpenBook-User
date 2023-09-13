package Project.OpenBook.Domain.Description.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DescriptionCreateDto {

    @NotBlank(message = "토픽 제목을 입력해주세요.")
    private String topicTitle;

    @NotNull(message = "하나 이상의 보기 내용을 입력해주세요.")
    private String[] contentList;
}
