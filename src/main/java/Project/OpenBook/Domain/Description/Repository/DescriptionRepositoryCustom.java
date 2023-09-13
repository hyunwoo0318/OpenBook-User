package Project.OpenBook.Domain.Description.Repository;

import Project.OpenBook.Domain.Description.Domain.Description;
import Project.OpenBook.Domain.Choice.Dto.DupChoiceDto;

import java.util.List;

public interface DescriptionRepositoryCustom {

    public Description findRandDescriptionByTopic(String topicTitle);

    public Description queryRandDescriptionByDescription(Long descriptionId);

    public List<Description> findDescriptionsByTopic(String topicTitle);

//    public List<DupChoiceDto> queryDupChoices(Long descriptionId, String topicTitle);

    public Description queryDescriptionByContent(String content);
}
