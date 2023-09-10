package Project.OpenBook.Topic.Repo;

import Project.OpenBook.Domain.PrimaryDate;
import Project.OpenBook.Topic.Domain.Topic;
import com.querydsl.core.Tuple;
import com.querydsl.core.group.Group;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface  TopicRepositoryCustom {

    public List<String> queryWrongTopicTitle(String topicTitle, int size);

    public List<Topic> queryTopicsByTopicTitleList(List<String> topicTitleList);

    public Optional<Topic> queryTopicWithCategoryChapter(String topicTitle);

    public Optional<Topic> queryTopicWithCategory(String topicTitle);
}
