package Project.OpenBook.Domain.ChoiceComment.ChoiceSentence;

import Project.OpenBook.Domain.Choice.Domain.Choice;
import Project.OpenBook.Domain.ChoiceComment.Service.Dto.ChoiceCommentInfoDto;

import java.util.List;
import java.util.Map;

public interface ChoiceSentenceRepositoryCustom {


    public Map<Choice, List<ChoiceCommentInfoDto>> queryChoiceSentences(List<Choice> choiceList);
}
