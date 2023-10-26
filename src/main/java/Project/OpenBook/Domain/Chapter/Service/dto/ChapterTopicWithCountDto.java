package Project.OpenBook.Domain.Chapter.Service.dto;

import Project.OpenBook.Domain.QuestionCategory.Domain.QuestionCategory;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChapterTopicWithCountDto {

    private String category;
    private Integer number;
    private String title;
    private String dateComment;
    private String era;
    private int descriptionCount;
    private int choiceCount;
    private int keywordCount;


    public ChapterTopicWithCountDto(Topic topic){
        QuestionCategory questionCategory = topic.getQuestionCategory();
        this.category = questionCategory.getCategory().getName();
        this.number = topic.getNumber();
        this.title = topic.getTitle();
        this.dateComment = topic.getDateComment();
        this.era = questionCategory.getEra().getName();
        this.descriptionCount = topic.getDescriptionList().size();
        this.choiceCount = topic.getChoiceList().size();
        this.keywordCount = topic.getKeywordList().size();
    }
}
