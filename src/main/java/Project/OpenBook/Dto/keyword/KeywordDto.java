package Project.OpenBook.Dto.keyword;

import Project.OpenBook.Domain.ImageFile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class KeywordDto {

    private String name;

    private String comment;

    private MultipartFile file;

    private Long id;
}
