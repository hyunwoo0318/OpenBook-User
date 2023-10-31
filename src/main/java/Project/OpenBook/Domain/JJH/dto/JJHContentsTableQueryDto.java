package Project.OpenBook.Domain.JJH.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JJHContentsTableQueryDto {
    private Boolean savedBookmark;
    private String title;
    private String content;
    private String state;
    private Integer contentNumber;
    private String dateComment;
    private String category;
}
