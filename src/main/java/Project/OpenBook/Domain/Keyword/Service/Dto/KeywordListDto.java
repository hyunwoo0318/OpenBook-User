package Project.OpenBook.Domain.Keyword.Service.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KeywordListDto {
    private List<String> keywordList;
}
