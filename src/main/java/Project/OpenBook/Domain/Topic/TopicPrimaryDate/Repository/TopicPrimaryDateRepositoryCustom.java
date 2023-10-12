package Project.OpenBook.Domain.Topic.TopicPrimaryDate.Repository;

import Project.OpenBook.Domain.Topic.TopicPrimaryDate.Domain.TopicPrimaryDate;

import java.util.List;

public interface TopicPrimaryDateRepositoryCustom {

    public List<TopicPrimaryDate> queryTopicPrimaryDateInChapter(Integer chapterNum);
}
