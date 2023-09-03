package Project.OpenBook.Dto.chapter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChapterInfoDto {

    @NotBlank(message = "단원 설명을 입력해주세요.")
    private String content;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChapterInfoDto)) return false;

        ChapterInfoDto that = (ChapterInfoDto) o;

        return Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return content != null ? content.hashCode() : 0;
    }
}
