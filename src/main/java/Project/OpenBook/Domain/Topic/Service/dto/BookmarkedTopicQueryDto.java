package Project.OpenBook.Domain.Topic.Service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookmarkedTopicQueryDto {
    private Integer chapterNumber;
    private String chapterTitle;
    private List<TopicListQueryDto> topicList = new ArrayList<>();
}
