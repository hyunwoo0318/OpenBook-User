package Project.OpenBook.Domain.WeightedRandomSelection;

import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WrongKeywordSelectModel{
    private Keyword keyword;
    private Integer record;
    private Integer usageCount;
    private Integer association;
    private Double totalScore;


}
