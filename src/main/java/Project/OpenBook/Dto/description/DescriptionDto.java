package Project.OpenBook.Dto.description;

import Project.OpenBook.Domain.Description;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DescriptionDto {

    private Long id;
    private String content;

    public DescriptionDto(Description description) {
        this.id = description.getId();
        this.content = description.getContent();
    }
}
