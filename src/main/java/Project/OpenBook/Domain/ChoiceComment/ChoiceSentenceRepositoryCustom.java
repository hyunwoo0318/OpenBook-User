package Project.OpenBook.Domain.ChoiceComment;

import Project.OpenBook.Domain.Choice.Domain.Choice;
import Project.OpenBook.Domain.ChoiceComment.ChoiceKeyword;
import Project.OpenBook.Domain.ChoiceComment.ChoiceSentence;
import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import Project.OpenBook.Domain.Sentence.Domain.Sentence;

import java.util.List;

public interface ChoiceSentenceRepositoryCustom {

    public List<ChoiceSentence> queryChoiceSentences(Choice inputChoice);
}
