package Project.OpenBook.Domain.WeightedRandomSelection.Model;

import Project.OpenBook.Domain.Topic.Domain.Topic;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TopicSelectModel{
    private Topic topic;
    private Integer record;
}
