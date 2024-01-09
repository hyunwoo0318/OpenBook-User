package Project.OpenBook.Domain.Chapter.Service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChapterDetailDto {

  private String title;
  private Integer number;
  private String dateComment;
  private int topicCount;

  @Builder
  public ChapterDetailDto(String title, Integer number, String dateComment, int topicCount) {
    this.title = title;
    this.number = number;
    this.dateComment = dateComment;
    this.topicCount = topicCount;
  }
}
