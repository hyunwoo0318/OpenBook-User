package Project.OpenBook.WeightedRandomSelection.Model;

import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KeywordSelectModel {
    private Keyword keyword;
    private Integer questionProb;
}
