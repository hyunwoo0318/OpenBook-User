package Project.OpenBook.Domain.DescriptionSentence;

import Project.OpenBook.Domain.Description.Domain.Description;

import java.util.List;

public interface DescriptionSentenceRepositoryCustom {

    public List<DescriptionSentence> queryDescriptionSentences(Description description);
}
