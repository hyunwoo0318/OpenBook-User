package Project.OpenBook.Domain.Description.Repository;

import Project.OpenBook.Domain.Description.Domain.Description;
import Project.OpenBook.Domain.Description.Service.DescriptionKeyword;
import Project.OpenBook.Domain.ExamQuestion.Domain.ExamQuestion;
import Project.OpenBook.Domain.ExamQuestion.Service.dto.ExamQuestionCommentDto;
import Project.OpenBook.Domain.Keyword.Domain.Keyword;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface DescriptionKeywordRepositoryCustom {

    public List<DescriptionKeyword> queryDescriptionKeywordsAdmin(Description description);

    public List<ExamQuestionCommentDto> queryDescriptionCustomer(Description description);

    public Optional<Description> queryDescription(Integer roundNumber, Integer questionNumber);

    public List<DescriptionKeyword> queryDescriptionKeywordsAdmin(String topicTitle);

    public Map<ExamQuestion, List<Keyword>> queryDescriptionKeywordsForInit();


}
