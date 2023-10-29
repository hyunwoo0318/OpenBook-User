package Project.OpenBook.Domain.Topic.Repo;

import Project.OpenBook.Domain.Topic.Domain.Topic;

import java.util.List;
import java.util.Optional;

public interface  TopicRepositoryCustom {

    public List<String> queryWrongTopicTitle(String topicTitle, int size);

    public List<Topic> queryTopicsByTopicTitleList(List<String> topicTitleList);

    public Optional<Topic> queryTopicWithQuestionCategory(String topicTitle);

    public List<Topic> queryTopicsWithQuestionCategory(Integer chapterNum);


    public Optional<Topic> queryTopicWithCategory(String topicTitle);

    public List<Topic> queryTopicsWithChapter();

    public List<Topic> queryTopicsInQuestionCategory(Long questionCategoryId);


}
