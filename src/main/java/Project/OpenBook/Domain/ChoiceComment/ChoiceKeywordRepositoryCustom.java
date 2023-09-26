package Project.OpenBook.Domain.ChoiceComment;

import Project.OpenBook.Domain.Choice.Domain.Choice;
import Project.OpenBook.Domain.Keyword.Domain.Keyword;

import java.util.List;

public interface ChoiceKeywordRepositoryCustom {

    public List<ChoiceKeyword> queryChoiceKeywords(Choice inputChoice);
}
