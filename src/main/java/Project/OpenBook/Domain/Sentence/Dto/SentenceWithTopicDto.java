package Project.OpenBook.Domain.Sentence.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SentenceWithTopicDto {

    private String name;
    private String topicTitle;
}
