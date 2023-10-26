package Project.OpenBook.Domain.Topic.Service.dto;

import Project.OpenBook.Domain.Keyword.Dto.KeywordUserDto;
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
public class TopicWithKeywordDto {

    private String category;
    private List<PrimaryDateDto> extraDateList;
    private List<KeywordUserDto> keywordList;

    public TopicWithKeywordDto(Topic topic) {
        this.category = topic.getQuestionCategory().getCategory().getName();
        this.extraDateList = topic.getTopicPrimaryDateList().stream()
                .map(pd -> new PrimaryDateDto(pd.getExtraDate(), pd.getExtraDateComment()))
                .sorted(Comparator.comparing(PrimaryDateDto::getExtraDate))
                .collect(Collectors.toList());
        this.keywordList = topic.getKeywordList().stream()
                .map(k -> new KeywordUserDto(k.getName(), k.getComment(),k.getImageUrl(), k.getDateComment(), k.getNumber(),
                        k.getKeywordPrimaryDateList().stream()
                                .map(p -> new PrimaryDateDto(p.getExtraDate(), p.getExtraDateComment()))
                                .collect(Collectors.toList())
                ))
                .sorted(Comparator.comparing(KeywordUserDto::getNumber))
                .collect(Collectors.toList());

    }
}
