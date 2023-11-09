package Project.OpenBook.Domain.DescriptionComment.DescriptionKeyword;

import Project.OpenBook.Domain.Description.Domain.Description;
import Project.OpenBook.Domain.ExamQuestion.Domain.ExamQuestion;
import Project.OpenBook.Domain.Topic.Domain.Topic;

import java.util.List;

public interface DescriptionKeywordRepositoryCustom {

    public List<DescriptionKeyword> queryDescriptionKeywordsForInit();

    public List<DescriptionKeyword> queryDescriptionKeywordsForTopicList(Description description);

    public List<DescriptionKeyword> queryDescriptionKeywordForExamQuestion(Integer roundNumber);
    public List<DescriptionKeyword> queryDescriptionKeywordForExamQuestion(List<ExamQuestion> examQuestionList);

    public List<DescriptionKeyword> queryDescriptionKeywordsForTopicList(String topicTitle);

    public List<DescriptionKeyword> queryDescriptionKeywordsForTopicList(Integer chapterNum);
    public List<DescriptionKeyword> queryDescriptionKeywordsForTopicList(List<Topic> topicList);


    public List<DescriptionKeyword> queryDescriptionKeywords(Long examQuestionId);


}
