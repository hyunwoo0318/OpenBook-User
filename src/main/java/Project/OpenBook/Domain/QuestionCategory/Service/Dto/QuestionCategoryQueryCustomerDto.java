package Project.OpenBook.Domain.QuestionCategory.Service.Dto;

import Project.OpenBook.Domain.QuestionCategory.Domain.QuestionCategory;
import Project.OpenBook.Domain.StudyHistory.QuestionCategoryLearningRecord.QuestionCategoryLearningRecord;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionCategoryQueryCustomerDto {

    private Long id;
    private String title;
    private Integer number;
    private Integer score;

    public QuestionCategoryQueryCustomerDto(QuestionCategoryLearningRecord record) {
        QuestionCategory questionCategory = record.getQuestionCategory();
        this.id = record.getId();
        this.title = questionCategory.getTitle();
        this.number = questionCategory.getNumber();
        this.score = (record.getAnswerCount() - record.getWrongCount());
    }

}
