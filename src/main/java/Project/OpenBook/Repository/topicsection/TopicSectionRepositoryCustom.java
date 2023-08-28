package Project.OpenBook.Repository.topicsection;

import Project.OpenBook.Domain.TopicSection;

import java.util.List;
import java.util.Optional;

public interface TopicSectionRepositoryCustom {

    public Optional<TopicSection> queryTopicSection(Long customerId, String topicTitle, String content);

    public List<TopicSection> queryTopicSections(Long customerId, Integer chapterNum);
}
