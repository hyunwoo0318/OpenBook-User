package Project.OpenBook.Repository.keyword;

import Project.OpenBook.Domain.Keyword;
import com.querydsl.core.Tuple;

import java.security.Key;
import java.util.List;
import java.util.Optional;

public interface KeywordRepositoryCustom {


    public Optional<Keyword> queryByNameInTopic(String name, String topicTitle);

    public List<Keyword> queryKeywordsByTopic(String topicTitle);

    public List<Keyword> queryKeywordsByTopic(String topicTitle, int size);

    public List<Keyword> queryKeywordsList(List<String> keywordNameList);

    public List<Tuple> queryWrongKeywords(String answerTopicTitle, int size);
}
