package Project.OpenBook.Domain.QuestionComment.DescriptionKeyword;

import Project.OpenBook.Domain.Topic.Domain.Topic;
import java.util.List;

public interface DescriptionKeywordRepositoryCustom {

    public List<DescriptionKeyword> queryDescriptionKeywordsForInit();


    public List<DescriptionKeyword> queryDescriptionKeywordForExamQuestion(Integer roundNumber);


    public List<DescriptionKeyword> queryDescriptionKeywordsForTopicList(String topicTitle);

    public List<DescriptionKeyword> queryDescriptionKeywordsForTopicList(Integer chapterNum);

    public List<DescriptionKeyword> queryDescriptionKeywordsForTopicList(List<Topic> topicList);


    public List<DescriptionKeyword> queryDescriptionKeywords(Long examQuestionId);


}
