package Project.OpenBook.Chapter.Service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChapterUserDto {
    private String title;
    private Integer number;
    private String state;
    private String progress;
}
