package Project.OpenBook.Domain.PrimaryDate.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PrimaryDateDto {
    private Integer extraDate;
    private Boolean extraDateCheck;
    private String extraDateComment;
}
