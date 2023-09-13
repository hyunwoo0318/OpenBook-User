package Project.OpenBook.Domain.Keyword.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class KeywordUserDto {
    private String name;
    private String comment;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KeywordUserDto)) return false;

        KeywordUserDto that = (KeywordUserDto) o;

        if (!Objects.equals(name, that.name)) return false;
        if (!Objects.equals(comment, that.comment)) return false;
        return Objects.equals(file, that.file);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        result = 31 * result + (file != null ? file.hashCode() : 0);
        return result;
    }

    private String file;
}