package Project.OpenBook.Domain.Search.Dto;

import Project.OpenBook.Domain.Topic.Domain.Topic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TopicSearchResultDto {

    private Integer chapterNumber;
    private String chapterTitle;
    private String topicTitle;

    public TopicSearchResultDto(Topic topic){
        this.chapterNumber = topic.getChapter().getNumber();
        this.chapterTitle = topic.getChapter().getTitle();
        this.topicTitle = topic.getTitle();
    }
}
