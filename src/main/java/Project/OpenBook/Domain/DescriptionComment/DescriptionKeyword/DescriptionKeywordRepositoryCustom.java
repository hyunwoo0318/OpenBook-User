package Project.OpenBook.Domain.DescriptionComment.DescriptionKeyword;

import Project.OpenBook.Domain.Description.Domain.Description;

import java.util.List;

public interface DescriptionKeywordRepositoryCustom {

    public List<DescriptionKeyword> queryDescriptionKeywordsAdmin(Description description);

    public List<DescriptionKeyword> queryDescriptionKeywordForExamQuestion(Integer roundNumber);

    public List<DescriptionKeyword> queryDescriptionKeywordsAdmin(String topicTitle);

    public List<DescriptionKeyword> queryDescriptionKeywords(Long examQuestionId);


}
