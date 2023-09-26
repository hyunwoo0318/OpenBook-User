package Project.OpenBook.Domain.Description.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DescriptionUpdateDto {

    @NotBlank(message = "보기 내용을 입력해주세요.")
    private String description;
}
