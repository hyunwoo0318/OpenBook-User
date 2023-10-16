package Project.OpenBook.Domain.ChoiceComment.ChoiceKeyword;

import Project.OpenBook.Domain.Choice.Domain.Choice;
import Project.OpenBook.Domain.ChoiceComment.Service.Dto.ChoiceCommentInfoDto;
import Project.OpenBook.Domain.ExamQuestion.Service.dto.ExamQuestionCommentDto;

import java.util.List;
import java.util.Map;

public interface ChoiceKeywordRepositoryCustom {

      public Map<Choice, List<ChoiceCommentInfoDto>> queryChoiceKeywordsAdmin(List<Choice> choiceList);

      public Map<Choice, List<ExamQuestionCommentDto>> queryChoiceKeywordsCustomer(List<Choice> choiceList);

      public List<ChoiceKeyword> queryChoiceKeywordsAdmin(String topicTitle);
}
