package Project.OpenBook.Domain.Question.Dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class QuizChoiceDto {

    private String choice;
    private String key;

    @Builder
    public QuizChoiceDto(String choice, String key) {
        this.choice = choice;
        this.key = key;
    }
}
