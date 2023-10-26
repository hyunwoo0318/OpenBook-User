package Project.OpenBook.Domain.QuestionCategory.Service.Dto;

import Project.OpenBook.Domain.QuestionCategory.Domain.QuestionCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QuestionCategoryQueryAdminDto {
    private Long id;
    private String title;
    private Integer number;
    private String era;
    private String category;
    private Integer topicCount;

    public QuestionCategoryQueryAdminDto(QuestionCategory questionCategory) {
        this.id = questionCategory.getId();
        this.number = questionCategory.getNumber();
        this.title = questionCategory.getTitle();
        this.era = questionCategory.getEra().getName();
        this.category = questionCategory.getCategory().getName();
        this.topicCount = questionCategory.getTopicList().size();
    }
}
