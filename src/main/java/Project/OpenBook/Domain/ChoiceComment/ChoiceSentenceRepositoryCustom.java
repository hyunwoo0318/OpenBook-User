package Project.OpenBook.Domain.ChoiceComment;

import Project.OpenBook.Domain.Choice.Domain.Choice;
import Project.OpenBook.Domain.ChoiceComment.ChoiceKeyword;
import Project.OpenBook.Domain.ChoiceComment.ChoiceSentence;
import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import Project.OpenBook.Domain.Sentence.Domain.Sentence;

import java.util.List;
import java.util.Map;

public interface ChoiceSentenceRepositoryCustom {

    public List<ChoiceSentence> queryChoiceSentences(Choice inputChoice);

    public Map<Choice, List<ChoiceCommentInfoDto>> queryChoiceSentenceTemp(Integer roundNumber, Integer questionNumber);
}
