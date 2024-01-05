package Project.OpenBook.Domain.Topic.Repo;

import Project.OpenBook.Domain.QuestionCategory.Domain.QuestionCategory;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import java.util.List;
import java.util.Optional;

public interface TopicRepositoryCustom {


    public List<Topic> queryTopicsWithKeywordList(int chapterNum);


    public Optional<Topic> queryTopicWithCategory(String topicTitle);

    public List<Topic> queryTopicsWithChapter();

    public List<Topic> queryTopicsInQuestionCategory(Long questionCategoryId);


    public List<Topic> queryTopicsWithCategory(int num);

    public List<Topic> queryTopicsInQuestionCategories(List<QuestionCategory> questionCategoryList);
}
