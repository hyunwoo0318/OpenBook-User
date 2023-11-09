package Project.OpenBook.Domain.Keyword.Service.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class KeywordWithTopicDto {
    private String name;
    private String topicTitle;
    private Long id;
}
