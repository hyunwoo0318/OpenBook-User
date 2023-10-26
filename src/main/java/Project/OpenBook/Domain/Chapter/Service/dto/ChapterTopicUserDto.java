package Project.OpenBook.Domain.Chapter.Service.dto;

import Project.OpenBook.Domain.QuestionCategory.Domain.QuestionCategory;
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
    private String dateComment;
    private String era;

    public ChapterTopicUserDto(Topic topic) {
        QuestionCategory questionCategory = topic.getQuestionCategory();
        this.title = topic.getTitle();
        this.dateComment = topic.getDateComment();
        this.era = questionCategory.getEra().getName();
        this.category = questionCategory.getCategory().getName();
    }
}
