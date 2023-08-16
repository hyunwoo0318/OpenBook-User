package Project.OpenBook.Dto.question;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class QuestionChoiceDto {

    /**
     * 키워드 -> 제목
     * 문장 -> 내용
     * 주제 -> 주제 제목
     */
    private String choice;

    /**
     * 키워드 -> 설명
     * 문장 -> X
     * 주제 -> X
     */
    private String comment;

    /**
     * 정답 주제 제목
     */
    private String key;

    @Builder
    public QuestionChoiceDto(String choice, String comment, String key) {
        this.choice = choice;
        this.comment = comment;
        this.key = key;
    }
}
