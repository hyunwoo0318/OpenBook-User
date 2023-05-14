package Project.OpenBook.Repository.dupcontent;

import Project.OpenBook.Domain.DupContent;
import Project.OpenBook.Service.DupContentService;

import java.util.Optional;

public interface DupContentRepositoryCustom {

    public DupContent queryDupContent(Long descriptionId, Long choiceId);
}
