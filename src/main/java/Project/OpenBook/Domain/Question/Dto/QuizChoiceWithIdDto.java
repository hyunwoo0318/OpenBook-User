package Project.OpenBook.Domain.Question.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class QuizChoiceWithIdDto {
    private QuizChoiceDto quizChoiceDto;
    private Long id;
}
