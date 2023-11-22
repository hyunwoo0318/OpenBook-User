package Project.OpenBook.Domain.Keyword.Repository;

import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import Project.OpenBook.Domain.QuestionCategory.Domain.QuestionCategory;
import Project.OpenBook.Domain.Topic.Domain.Topic;

import java.util.List;
import java.util.Optional;

public interface KeywordRepositoryCustom {


    /**
     * 특정 주제에 특정 이름을 가진 키워드를 쿼리하는 메서드
     * @param keywordName 키워드 이름
     * @param topicTitle 특정 주제의 제목
     * @return Optional값을 감싸서 리턴
     */
    public Optional<Keyword> queryByNameInTopic(String keywordName, String topicTitle);

    /**
     * 특정 주제 내의 모든 키워드를 limit 개수만큼 쿼리하는 메서드
     * @param topicTitle 특정 주제의 제목
     * @param limit 쿼리할 키워드의 최대개수
     * @return limit보다 특정 주제 내의 모든 키워드의 개수가 적으면 모든 키워드를 리턴하고
     * limit보다 특정 주제내의 모든 키워드의 개수가 크면 limit개수만큼 키워드를 리턴한다.
     */
    public List<Keyword> queryKeywordsInTopicWithLimit(String topicTitle, int limit);

    public List<Keyword> queryWrongKeywords(String answerTopicTitle, int limit);

    public List<Keyword> queryWrongKeywords(List<String> keywordNameList, String answerTopicTitle);

    public List<Keyword> queryKeywordsInTopic(String topicTitle);

    public List<Keyword> queryKeywordsInTopicWithPrimaryDate(String topicTitle);
    public List<Keyword> queryKeywordsInTopicWithPrimaryDate(Integer chapterNum);
    public List<Keyword> queryKeywordsInTopicWithPrimaryDate(List<Topic> topicList);

    public List<Keyword> queryKeywordsWithChapter();

    public List<Keyword> queryKeywordsForUpdateHistory(List<Long> keywordIdList);

    public List<Keyword> queryKeywordsInQuestionCategory(QuestionCategory questionCategory);

    public List<Keyword> queryKeywordsWithTopic();

    public List<Keyword> queryKeywordsForInit();

    public List<Keyword> queryRandomOpenedKeywords(Topic answerTopic, Integer count);

    public List<Keyword> queryKeywordsInQuestionCategories(List<Keyword> keywordList);

}
