package Project.OpenBook.Domain.ChoiceComment;

import Project.OpenBook.Domain.Choice.Domain.Choice;
import Project.OpenBook.Domain.Keyword.Domain.Keyword;

import java.util.List;
import java.util.Map;

public interface ChoiceKeywordRepositoryCustom {

      public Map<Choice, List<ChoiceCommentInfoDto>> queryChoiceKeywords(List<Choice> choiceList);
}
