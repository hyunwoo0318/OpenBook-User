package Project.OpenBook.Dto.topic;

import Project.OpenBook.Dto.primaryDate.PrimaryDateUserDto;
import Project.OpenBook.Dto.keyword.KeywordUserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TopicCustomerDto {

    private String category;
    private Integer startDate;
    private Integer endDate;
    private List<PrimaryDateUserDto> extraDateList;
    private List<KeywordUserDto> keywordList;
    private List<String> sentenceList;
}
