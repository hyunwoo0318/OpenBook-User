package Project.OpenBook.Repository.description;

import Project.OpenBook.Domain.Description;

public interface DescriptionRepositoryCustom {

    public Description findRandDescriptionByTopic(String topicTitle);
}
