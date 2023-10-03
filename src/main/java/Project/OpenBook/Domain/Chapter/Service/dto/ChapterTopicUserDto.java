package Project.OpenBook.Domain.Chapter.Service.dto;

import Project.OpenBook.Domain.Topic.Domain.Topic;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChapterTopicUserDto {
    private String title;
    private String category;

    public ChapterTopicUserDto(Topic topic) {
        this.title = topic.getTitle();
        this.category = topic.getCategory().getName();
    }
}
