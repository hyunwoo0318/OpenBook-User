package Project.OpenBook.Domain.Topic.Service.dto;

import Project.OpenBook.Domain.Keyword.Service.Dto.KeywordDto;
import Project.OpenBook.Domain.QuestionCategory.Domain.QuestionCategory;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TopicListQueryDto {
    private String title;
    private String dateComment;
    private String category;
    private String era;
    private List<PrimaryDateDto> extraDateList;
    private List<KeywordDto> keywordList;
    private Boolean isBookmarked;

    public TopicListQueryDto(Boolean isBookmarked, Topic topic, List<KeywordDto> keywordList) {
        this.isBookmarked = isBookmarked;
        QuestionCategory questionCategory = topic.getQuestionCategory();
        this.title = topic.getTitle();
        this.dateComment = topic.getDateComment();
        this.category = questionCategory.getCategory().getName();
        this.era = questionCategory.getEra().getName();
        this.extraDateList = topic.getTopicPrimaryDateList().stream()
                .map(pd -> new PrimaryDateDto(pd.getExtraDate(), pd.getExtraDateComment()))
                .sorted(Comparator.comparing(PrimaryDateDto::getExtraDate))
                .collect(Collectors.toList());
        this.keywordList = keywordList;
    }
}
