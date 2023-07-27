package Project.OpenBook.Dto.keyword;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class KeywordWithTopicDto {
    private String name;
    private String comment;
    private String topicTitle;
}
