package Project.OpenBook.Domain.Chapter.Service.dto;

import Project.OpenBook.Domain.QuestionCategory.Domain.QuestionCategory;
import Project.OpenBook.Domain.QuestionCategory.Service.Dto.QuestionCategoryTitleDto;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChapterTopicWithCountDto {

    private Integer number;
    private String title;
    private String dateComment;
    private QuestionCategoryTitleDto questionCategory;
    private int descriptionCount;
    private int choiceCount;
    private int keywordCount;


    public ChapterTopicWithCountDto(Topic topic){
        QuestionCategory findQuestionCategory = topic.getQuestionCategory();
        this.number = topic.getNumber();
        this.title = topic.getTitle();
        this.dateComment = topic.getDateComment();
        questionCategory = new QuestionCategoryTitleDto(findQuestionCategory.getTitle());
        this.descriptionCount = topic.getDescriptionList().size();
        this.choiceCount = topic.getChoiceList().size();
        this.keywordCount = topic.getKeywordList().size();
    }
}
