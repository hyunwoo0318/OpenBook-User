package Project.OpenBook.Domain.Topic.Service.dto;

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
