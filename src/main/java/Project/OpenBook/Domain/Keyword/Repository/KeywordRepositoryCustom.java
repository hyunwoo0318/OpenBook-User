package Project.OpenBook.Domain.Keyword.Repository;

import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import Project.OpenBook.Domain.Topic.Domain.Topic;

import java.util.List;

public interface KeywordRepositoryCustom {

    public List<Keyword> queryWrongKeywords(List<String> keywordNameList, String answerTopicTitle);

    public List<Keyword> queryKeywordsInTopic(String topicTitle);

    public List<Keyword> searchKeywords(String input);

    public List<Keyword> queryKeywordsInTopicWithPrimaryDate(String topicTitle);

    public List<Keyword> queryKeywordsInTopicWithPrimaryDate(Integer chapterNum);

    public List<Keyword> queryKeywordsInTopicWithPrimaryDate(List<Topic> topicList);

    public List<Keyword> queryKeywordsWithChapter();

    public List<Keyword> queryKeywordsForUpdateHistory(List<Long> keywordIdList);

    public List<Keyword> queryKeywordsForInit();

    public List<Keyword> queryRandomOpenedKeywords(Topic answerTopic, Integer count);

    public List<Keyword> queryKeywordsInQuestionCategories(List<Keyword> keywordList);
}
