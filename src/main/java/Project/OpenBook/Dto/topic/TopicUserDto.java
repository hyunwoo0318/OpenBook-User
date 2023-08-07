package Project.OpenBook.Dto.topic;

import Project.OpenBook.Dto.PrimaryDate.PrimaryDateDto;
import Project.OpenBook.Dto.PrimaryDate.PrimaryDateUserDto;
import Project.OpenBook.Dto.keyword.KeywordUserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TopicUserDto {
    private String title;
    private String category;
    private Integer startDate;
    private Integer endDate;
    private String detail;
    private List<String> sentenceList;
    private List<KeywordUserDto> keywordList;
    private List<PrimaryDateUserDto> dateList;
}
