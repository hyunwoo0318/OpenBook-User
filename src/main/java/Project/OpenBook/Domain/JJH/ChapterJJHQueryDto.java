package Project.OpenBook.Domain.JJH;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChapterJJHQueryDto {
    private Integer number;
    private Integer jjhNumber;
    private String title;
    private Long id;
}
