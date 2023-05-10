package Project.OpenBook.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookmarkDto {

    @NotNull(message = "회원의 아이디를 적어주세요.")
    private Long customerId;

    @NotBlank(message = "저장할 topic의 제목을 입력해주세요.")
    private String topicTitle;
}
