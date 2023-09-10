package Project.OpenBook.Chapter.Controller.dto;

import Project.OpenBook.Topic.Domain.Topic;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChapterTopicUserDto {
    private String title;
    private String category;
    private Integer startDate;
    private Integer endDate;

    public ChapterTopicUserDto(Topic topic) {
        this.title = topic.getTitle();
        this.category = topic.getCategory().getName();
        this.startDate = topic.getStartDate();
        this.endDate = topic.getEndDate();
    }
}
