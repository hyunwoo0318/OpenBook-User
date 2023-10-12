package Project.OpenBook.Domain.ChoiceComment.ChoiceKeyword;

import Project.OpenBook.Domain.Choice.Domain.Choice;
import Project.OpenBook.Domain.ChoiceComment.Service.Dto.ChoiceCommentInfoDto;

import java.util.List;
import java.util.Map;

public interface ChoiceKeywordRepositoryCustom {

      public Map<Choice, List<ChoiceCommentInfoDto>> queryChoiceKeywords(List<Choice> choiceList);

      public List<ChoiceKeyword> queryChoiceKeywords(String topicTitle);
}
