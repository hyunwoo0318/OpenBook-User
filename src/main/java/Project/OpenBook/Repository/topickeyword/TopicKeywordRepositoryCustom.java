package Project.OpenBook.Repository.topickeyword;

import Project.OpenBook.Domain.Keyword;
import Project.OpenBook.Domain.Topic;
import Project.OpenBook.Domain.TopicKeyword;

import java.util.List;

public interface TopicKeywordRepositoryCustom {

    public void deleteTopicKeyword(String topicTitle, String keywordName);

    public TopicKeyword queryTopicKeyword(String topicTitle, String keywordName);

    public List<TopicKeyword> queryTopicKeyword(String topicTitle);

    public List<Topic> queryTopicsByKeyword(String keywordName);
}
