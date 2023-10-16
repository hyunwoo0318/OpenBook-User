package Project.OpenBook.Domain.Question.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChoiceTempDto {

    private String choice;
    private String key;

    @Builder
    public ChoiceTempDto(String choice, String key) {
        this.choice = choice;
        this.key = key;
    }
}
