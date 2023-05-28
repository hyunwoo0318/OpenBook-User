package Project.OpenBook.Repository.keyword;

import Project.OpenBook.Domain.Keyword;

import java.security.Key;
import java.util.List;

public interface KeywordRepositoryCustom {


    public List<String> queryKeywordTopic(String keywordName);

    public List<Keyword> queryKeywordsList(List<String> keywordNameList);
}
