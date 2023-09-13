package Project.OpenBook.Domain.Topic.Service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TopicTitleListDto {
    private List<String> topicList;
}
