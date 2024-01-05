package Project.OpenBook.Domain.AnswerNote.Service.Dto;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AnswerNoteDto {

    @NotNull(message = "저장할 문제의 아이디를 입력해주세요.")
    private Long questionId;
}
