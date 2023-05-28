package Project.OpenBook.Dto.keyword;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class KeywordDto {

    @NotBlank(message = "카테고리 이름을 입력해주세요")
    private String name;
}
