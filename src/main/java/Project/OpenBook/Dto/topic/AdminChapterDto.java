package Project.OpenBook.Dto.topic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdminChapterDto {

    private String category;

    private String title;

    private Integer startDate;

    private Integer endDate;

    private Long descriptionCount;

    private Long choiceCount;

    private Long keywordCount;
}
