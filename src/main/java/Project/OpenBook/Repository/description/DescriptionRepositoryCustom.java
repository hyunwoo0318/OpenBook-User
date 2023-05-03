package Project.OpenBook.Repository.description;

import Project.OpenBook.Domain.Description;

import java.util.List;

public interface DescriptionRepositoryCustom {

    public Description findRandDescriptionByTopic(String topicTitle);

    public Description queryRandDescriptionByDescription(Long descriptionId);

    public List<Description> findDescriptionsByTopic(String topicTitle);
}
