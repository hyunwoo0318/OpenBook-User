package Project.OpenBook.Domain.Keyword.Dto;

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
