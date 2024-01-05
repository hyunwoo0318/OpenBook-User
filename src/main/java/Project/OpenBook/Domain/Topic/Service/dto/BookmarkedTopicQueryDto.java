package Project.OpenBook.Domain.Topic.Service.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookmarkedTopicQueryDto {

    private Integer chapterNumber;
    private String chapterTitle;
    private List<TopicListQueryDto> topicList = new ArrayList<>();
}
