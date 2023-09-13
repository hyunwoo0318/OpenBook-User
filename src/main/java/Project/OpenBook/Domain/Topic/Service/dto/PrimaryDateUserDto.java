package Project.OpenBook.Domain.Topic.Service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PrimaryDateUserDto {
    private Integer extraDate;
    private String extraDateComment;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PrimaryDateUserDto)) return false;

        PrimaryDateUserDto that = (PrimaryDateUserDto) o;

        if (extraDate != null ? !extraDate.equals(that.extraDate) : that.extraDate != null) return false;
        if (extraDateComment != null ? !extraDateComment.equals(that.extraDateComment) : that.extraDateComment != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = extraDate != null ? extraDate.hashCode() : 0;
        result = 31 * result + (extraDateComment != null ? extraDateComment.hashCode() : 0);
        return result;
    }
}
