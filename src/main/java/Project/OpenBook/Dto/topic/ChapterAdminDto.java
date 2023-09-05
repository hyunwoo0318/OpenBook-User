package Project.OpenBook.Dto.topic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChapterAdminDto {

    private String category;

    private Integer number;

    private String title;

    private Integer startDate;

    private Integer endDate;

    private int descriptionCount;

    private int choiceCount;

    private int keywordCount;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChapterAdminDto)) return false;

        ChapterAdminDto that = (ChapterAdminDto) o;

        if (!Objects.equals(category, that.category)) return false;
        if (!Objects.equals(title, that.title)) return false;
        if (!Objects.equals(startDate, that.startDate)) return false;
        if (!Objects.equals(endDate, that.endDate)) return false;
        if (!Objects.equals(descriptionCount, that.descriptionCount))
            return false;
        if (!Objects.equals(choiceCount, that.choiceCount)) return false;
        return Objects.equals(keywordCount, that.keywordCount);
    }

}
