package Project.OpenBook.Topic.Controller.dto;

import Project.OpenBook.Domain.Sentence;
import Project.OpenBook.Dto.primaryDate.PrimaryDateDto;
import Project.OpenBook.Dto.primaryDate.PrimaryDateUserDto;
import Project.OpenBook.Dto.keyword.KeywordUserDto;
import Project.OpenBook.Topic.Domain.Topic;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TopicWithKeywordSentenceDto {

    private String category;
    private Integer startDate;
    private Integer endDate;
    private List<PrimaryDateUserDto> extraDateList;
    private List<KeywordUserDto> keywordList;
    private List<String> sentenceList;

    public TopicWithKeywordSentenceDto(Topic topic) {
        this.category = topic.getCategory().getName();
        this.startDate = topic.getStartDate();
        this.endDate = topic.getEndDate();
        this.extraDateList = topic.getPrimaryDateList().stream()
                .map(pd -> new PrimaryDateUserDto(pd.getExtraDate(), pd.getExtraDateComment()))
                .collect(Collectors.toList());
        this.keywordList = topic.getKeywordList().stream()
                .map(k -> new KeywordUserDto(k.getName(), k.getComment(), k.getImageUrl()))
                .collect(Collectors.toList());
        this.sentenceList = topic.getSentenceList().stream()
                .map(Sentence::getName)
                .collect(Collectors.toList());
    }
}
