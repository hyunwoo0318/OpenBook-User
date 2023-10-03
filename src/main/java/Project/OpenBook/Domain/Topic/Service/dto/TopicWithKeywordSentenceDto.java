package Project.OpenBook.Domain.Topic.Service.dto;

import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import Project.OpenBook.Domain.Sentence.Domain.Sentence;
import Project.OpenBook.Domain.Keyword.Dto.KeywordUserDto;
import Project.OpenBook.Domain.Topic.Domain.Topic;
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
    private List<PrimaryDateDto> extraDateList;
    private List<KeywordUserDto> keywordList;
    private List<String> sentenceList;

    public TopicWithKeywordSentenceDto(Topic topic) {
        this.category = topic.getCategory().getName();
        this.extraDateList = topic.getTopicPrimaryDateList().stream()
                .map(pd -> new PrimaryDateDto(pd.getExtraDate(), pd.getExtraDateComment()))
                .collect(Collectors.toList());
        this.keywordList = topic.getKeywordList().stream()
                .map(k -> new KeywordUserDto(k.getName(), k.getComment(), k.getImageUrl(),
                        k.getKeywordPrimaryDateList().stream()
                                .map(p -> new PrimaryDateDto(p.getExtraDate(), p.getExtraDateComment()))
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
        this.sentenceList = topic.getSentenceList().stream()
                .map(Sentence::getName)
                .collect(Collectors.toList());
    }
}
