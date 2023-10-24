package Project.OpenBook.Domain.JJH.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChapterJJHAdminQueryDto {
    private Integer number;
    private String title;
    private Integer jjhNumber;
    private Long id;
}
