package Project.OpenBook.Domain.Sentence.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SentenceUpdateDto {

    @NotBlank(message = "내용을 입력해주세요.")
    private String name;
}
