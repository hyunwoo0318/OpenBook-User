package Project.OpenBook.Domain.StudyProgress.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TopicProgressAddDto {

    @NotBlank(message = "키워드 이름을 입력해주세요.")
    private String keywordName;

    private Integer count;

}