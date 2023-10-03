package Project.OpenBook.Domain.ChoiceComment.Service.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChoiceInfoDto {
    private String choice;
    private Integer choiceNumber;
    private String choiceType;
}
