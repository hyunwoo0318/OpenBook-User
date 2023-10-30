package Project.OpenBook.Domain.JJH.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChapterJJHCustomerQueryDto {
    private String title;
    private Integer number;
    private String state;
    private Integer jjhNumber;
    private String dateComment;
}
