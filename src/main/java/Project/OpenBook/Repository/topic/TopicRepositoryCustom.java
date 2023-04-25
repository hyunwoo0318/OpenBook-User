package Project.OpenBook.Repository.topic;

import Project.OpenBook.Domain.Topic;

public interface TopicRepositoryCustom {

    public Topic queryRandTopicByCategory(String categoryName);
}
