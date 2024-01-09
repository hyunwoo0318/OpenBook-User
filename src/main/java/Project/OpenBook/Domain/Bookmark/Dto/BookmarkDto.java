package Project.OpenBook.Domain.Bookmark.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookmarkDto {

  @Schema(description = "북마크에 추가할 토픽 제목", example = "고조선")
  @NotBlank(message = "토픽 제목을 입력해주세요.")
  private String topicTitle;
}
