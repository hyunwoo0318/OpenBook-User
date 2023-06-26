package Project.OpenBook.Repository.topic;

import Project.OpenBook.Domain.Topic;
import Project.OpenBook.Dto.topic.AdminChapterDto;

import java.util.List;

public interface TopicRepositoryCustom {

    public Topic queryRandTopicByCategory(String categoryName);

    public Topic queryTopicByDescription(Long descriptionId);

    public Topic queryTopicByChoice(Long choiceId);

    public List<String> queryTopicKeywords(String topicTitle);

    public List<AdminChapterDto> queryAdminChapterDto(Integer chapterNum);
}
