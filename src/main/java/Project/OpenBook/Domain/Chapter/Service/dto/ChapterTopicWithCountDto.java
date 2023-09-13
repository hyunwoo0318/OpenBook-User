package Project.OpenBook.Domain.Chapter.Service.dto;

import Project.OpenBook.Domain.Topic.Domain.Topic;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChapterTopicWithCountDto {

    private String category;
    private Integer number;
    private String title;
    private Integer startDate;
    private Integer endDate;
    private int descriptionCount;
    private int choiceCount;
    private int keywordCount;


    public ChapterTopicWithCountDto(Topic topic){
        this.category = topic.getCategory().getName();
        this.number = topic.getNumber();
        this.title = topic.getTitle();
        this.startDate = topic.getStartDate();
        this.endDate = topic.getEndDate();
        this.descriptionCount = topic.getDescriptionList().size();
        this.choiceCount = topic.getChoiceList().size();
        this.keywordCount = topic.getKeywordList().size();
    }
}
