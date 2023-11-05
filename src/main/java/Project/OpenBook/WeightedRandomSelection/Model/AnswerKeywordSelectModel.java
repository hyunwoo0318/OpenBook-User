package Project.OpenBook.WeightedRandomSelection.Model;

import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import lombok.Getter;
import lombok.Setter;

@Getter
public class AnswerKeywordSelectModel{
    private Keyword keyword;
    private Integer record;
    private Integer usageCount;
    @Setter
    private Double totalScore;

    public AnswerKeywordSelectModel(Keyword keyword, Integer record, Integer usageCount) {
        this.keyword = keyword;
        this.record = record;
        this.usageCount = usageCount;
    }
}
