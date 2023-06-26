package Project.OpenBook.Repository.topickeyword;

import Project.OpenBook.Domain.Keyword;
import Project.OpenBook.Domain.TopicKeyword;

public interface TopicKeywordRepositoryCustom {

    public void deleteTopicKeyword(String topicTitle, String keywordName);

    public TopicKeyword queryTopicKeyword(String topicTitle, String keywordName);
}
