package Project.OpenBook.Repository.keyword;

import Project.OpenBook.Domain.Keyword;
import com.querydsl.core.Tuple;

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
     * 특정 주제 내의 모든 키워드를 쿼리하는 메서드
     * @param topicTitle 특정 주제의 제목
     * @return 전체 키워드 값을 리스트로 리턴
     */
    public List<Keyword> queryKeywordsInTopic(String topicTitle);

    /**
     * 특정 주제 내의 모든 키워드를 limit 개수만큼 쿼리하는 메서드
     * @param topicTitle 특정 주제의 제목
     * @param limit 쿼리할 키워드의 최대개수
     * @return limit보다 특정 주제 내의 모든 키워드의 개수가 적으면 모든 키워드를 리턴하고
     * limit보다 특정 주제내의 모든 키워드의 개수가 크면 limit개수만큼 키워드를 리턴한다.
     */
    public List<Keyword> queryKeywordsInTopicWithLimit(String topicTitle, int limit);
//
//    public List<Keyword> queryKeywordsList(List<String> keywordNameList);

    /**
     * 정답 주제를 제외한 나머지 주제에서 limit만큼 키워드정보를 쿼리하는 메서드
     * @param answerTopicTitle 정답 주제 제목
     * @param limit 쿼리할 키워드의 최대 개수
     * @return {keyword.name, keyword.comment, keyword.topic.title} 리턴
     */
    public List<Tuple> queryWrongKeywords(String answerTopicTitle, int limit);
}
