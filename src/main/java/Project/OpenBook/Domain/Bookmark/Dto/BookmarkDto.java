package Project.OpenBook.Domain.Bookmark.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookmarkDto {

    private String topicTitle;

    private Long id;
}
