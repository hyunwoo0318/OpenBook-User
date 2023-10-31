package Project.OpenBook.Domain.AnswerNote.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnswerNoteDto {

    @NotNull(message = "저장할 문제의 아이디를 입력해주세요.")
    private Long id;
}
