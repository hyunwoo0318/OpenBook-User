package Project.OpenBook.Domain.Topic.Service.dto;

import Project.OpenBook.Domain.Keyword.Service.Dto.KeywordDto;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TopicWithKeywordDto {

    private String category;
    private List<PrimaryDateDto> extraDateList;
    private List<KeywordDto> keywordList;

    public TopicWithKeywordDto(Topic topic, List<KeywordDto> keywordList) {
        this.category = topic.getQuestionCategory().getCategory().getName();
        this.extraDateList = topic.getTopicPrimaryDateList().stream()
            .map(pd -> new PrimaryDateDto(pd.getExtraDate(), pd.getExtraDateComment()))
            .sorted(Comparator.comparing(PrimaryDateDto::getExtraDate))
            .collect(Collectors.toList());
        this.keywordList = keywordList;
    }
}
