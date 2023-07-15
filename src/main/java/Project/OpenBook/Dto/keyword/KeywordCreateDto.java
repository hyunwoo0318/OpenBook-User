package Project.OpenBook.Dto.keyword;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class KeywordCreateDto {

    @NotBlank(message = "키워드 이름을 입력해주세요.")
    private String name;
    private String comment;

    @NotBlank(message = "토픽제목을 입력해주세요.")
    private String topic;

    private MultipartFile file;

}
