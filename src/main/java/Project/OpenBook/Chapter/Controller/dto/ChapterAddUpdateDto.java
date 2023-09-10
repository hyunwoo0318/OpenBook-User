package Project.OpenBook.Chapter.Controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChapterAddUpdateDto {

    private String title;
    private int number;
    private Integer startDate;
    private Integer endDate;
}
