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

    private String title;

    private Integer startDate;

    private Integer endDate;

    private Long descriptionCount;

    private Long choiceCount;

    private Long keywordCount;

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

    @Override
    public int hashCode() {
        int result = category != null ? category.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        result = 31 * result + (descriptionCount != null ? descriptionCount.hashCode() : 0);
        result = 31 * result + (choiceCount != null ? choiceCount.hashCode() : 0);
        result = 31 * result + (keywordCount != null ? keywordCount.hashCode() : 0);
        return result;
    }
}
