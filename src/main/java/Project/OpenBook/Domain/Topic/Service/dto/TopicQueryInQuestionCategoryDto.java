package Project.OpenBook.Domain.Topic.Service.dto;

import Project.OpenBook.Domain.Topic.Domain.Topic;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TopicQueryInQuestionCategoryDto {
    private String title;
    private String category;
    private String dateComment;
    private Integer number;
    private Boolean isBookmarked;

    public TopicQueryInQuestionCategoryDto(Boolean isBookmarked, Topic topic) {
        this.isBookmarked = isBookmarked;
        this.title = topic.getTitle();
        this.category = topic.getQuestionCategory().getCategory().getName();
        this.dateComment = topic.getDateComment();
        this.number = topic.getNumber();
    }
}
