package Project.OpenBook.Domain.Topic.Service.dto;

import Project.OpenBook.Domain.Keyword.Dto.KeywordDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class TopicWithKeywordDto {

    private String category;
    private List<PrimaryDateDto> extraDateList;
    private List<KeywordDto> keywordList;

//    public TopicWithKeywordDto(Topic topic) {
//        this.category = topic.getQuestionCategory().getCategory().getName();
//        this.extraDateList = topic.getTopicPrimaryDateList().stream()
//                .map(pd -> new PrimaryDateDto(pd.getExtraDate(), pd.getExtraDateComment()))
//                .sorted(Comparator.comparing(PrimaryDateDto::getExtraDate))
//                .collect(Collectors.toList());
//        this.keywordList = topic.getKeywordList().stream()
//                .map(k -> new KeywordUserDto(k.getName(), k.getComment(),k.getImageUrl(), k.getDateComment(), k.getNumber(),
//                        k.getKeywordPrimaryDateList().stream()
//                                .map(p -> new PrimaryDateDto(p.getExtraDate(), p.getExtraDateComment()))
//                                .collect(Collectors.toList())
//                ))
//                .sorted(Comparator.comparing(KeywordUserDto::getNumber))
//                .collect(Collectors.toList());
//
//    }


    public TopicWithKeywordDto(String category, List<PrimaryDateDto> extraDateList, List<KeywordDto> keywordList) {
        this.category = category;
        this.extraDateList = extraDateList;
        this.keywordList = keywordList;
    }
}
