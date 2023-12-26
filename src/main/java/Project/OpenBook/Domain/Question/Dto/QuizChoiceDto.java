package Project.OpenBook.Domain.Question.Dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class QuizChoiceDto {

    private String choice;
    private String key;
    private String file;

    @Builder
    public QuizChoiceDto(String choice, String key, String file) {
        this.choice = choice;
        this.key = key;
        this.file = file;
    }
}
