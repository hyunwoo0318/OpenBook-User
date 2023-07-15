package Project.OpenBook.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PrimaryDateDto {
    private Integer date;
    private Boolean dateCheck;
    private String dateComment;
}
