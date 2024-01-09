package Project.OpenBook.Domain.AnswerNote.Service.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AnswerNoteDto {

    @NotNull(message = "저장할 문제의 아이디를 입력해주세요.")
    @Schema(description = "저장할 문제의 ID", example = "1")
    private Long questionId;
}
