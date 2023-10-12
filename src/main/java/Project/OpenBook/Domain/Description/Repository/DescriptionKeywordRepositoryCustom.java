package Project.OpenBook.Domain.Description.Repository;

import Project.OpenBook.Domain.Description.Domain.Description;
import Project.OpenBook.Domain.Description.Service.DescriptionKeyword;

import java.util.List;
import java.util.Optional;

public interface DescriptionKeywordRepositoryCustom {

    public List<DescriptionKeyword> queryDescriptionKeywords(Description description);

    public Optional<Description> queryDescription(Integer roundNumber, Integer questionNumber);

    public List<DescriptionKeyword> queryDescriptionKeywords(String topicTitle);


}
