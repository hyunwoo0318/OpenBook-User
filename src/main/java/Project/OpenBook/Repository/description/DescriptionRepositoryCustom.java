package Project.OpenBook.Repository.description;

import Project.OpenBook.Domain.Description;
import Project.OpenBook.Dto.choice.DupChoiceDto;

import java.util.List;

public interface DescriptionRepositoryCustom {

    public Description findRandDescriptionByTopic(String topicTitle);

    public Description queryRandDescriptionByDescription(Long descriptionId);

    public List<Description> findDescriptionsByTopic(String topicTitle);

    public List<DupChoiceDto> queryDupChoices(Long descriptionId, String topicTitle);

    public Description queryDescriptionByContent(String content);
}
