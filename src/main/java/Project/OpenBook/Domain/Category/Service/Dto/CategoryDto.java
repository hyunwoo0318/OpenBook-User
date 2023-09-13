package Project.OpenBook.Domain.Category.Service.Dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {

    @Schema(name = "카테고리 이름", required = true, example = "유물")
    @NotBlank(message = "카테고리 이름을 입력해주세요")
    private String name;
}
