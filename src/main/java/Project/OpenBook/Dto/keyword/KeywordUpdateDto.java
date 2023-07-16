package Project.OpenBook.Dto.keyword;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class KeywordUpdateDto {

    private String name;
    private String comment;

    private List<String> fileList;
}
