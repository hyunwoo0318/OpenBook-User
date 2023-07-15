package Project.OpenBook.Repository.keyword;

import Project.OpenBook.Domain.Keyword;

import java.security.Key;
import java.util.List;

public interface KeywordRepositoryCustom {

    public Keyword queryByNameInTopic(String name, String topicTitle);

    public List<Keyword> queryKeywordsByTopic(String topicTitle);

    public List<Keyword> queryKeywordsList(List<String> keywordNameList);
}
