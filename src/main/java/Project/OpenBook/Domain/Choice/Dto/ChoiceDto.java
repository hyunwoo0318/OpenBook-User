package Project.OpenBook.Domain.Choice.Dto;

import Project.OpenBook.Domain.Choice.Domain.Choice;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChoiceDto {

    //topicTitle

    private String content;

    private Long id;

    public ChoiceDto(Choice choice) {
        this.id = choice.getId();
        this.content = choice.getContent();
    }

}
