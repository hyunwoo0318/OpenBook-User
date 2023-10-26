package Project.OpenBook.Domain.QuestionCategory.Service.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionCategoryAddUpdateDto {

    private String title;
    private String era;
    private String category;
}
