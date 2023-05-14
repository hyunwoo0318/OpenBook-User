package Project.OpenBook.Service;

import Project.OpenBook.Domain.Choice;
import Project.OpenBook.Domain.Description;
import Project.OpenBook.Domain.DupContent;
import Project.OpenBook.Dto.choice.ChoiceIdListDto;
import Project.OpenBook.Repository.dupcontent.DupContentRepository;
import Project.OpenBook.Repository.choice.ChoiceRepository;
import Project.OpenBook.Repository.description.DescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DupContentService {

    private final DupContentRepository dupContentRepository;
    private final DescriptionRepository descriptionRepository;

    private final ChoiceRepository choiceRepository;


    @Transactional
    public List<DupContent> addDupContentChoices(Long descriptionId, ChoiceIdListDto choiceIdListDto) {
        List<Long> choiceIdList = choiceIdListDto.getChoiceIdList();
        Optional<Description> descriptionOptional = descriptionRepository.findById(descriptionId);
        if (descriptionOptional.isEmpty()) {
            return null;
        }
        Description description = descriptionOptional.get();

        List<Choice> choiceList = choiceRepository.queryChoicesById(choiceIdList);
        if(choiceList.size() != choiceIdList.size()){
            return null;
        }

        List<DupContent> dupContentList = new ArrayList<>();
        for (Choice choice : choiceList) {
            DupContent dupContent = new DupContent(description, choice);
            dupContentList.add(dupContent);
        }

        dupContentRepository.saveAll(dupContentList);
        return dupContentList;
    }

    public boolean deleteDupContentChoices(Long descriptionId, Long choiceId) {
        DupContent dupContent = dupContentRepository.queryDupContent(descriptionId, choiceId);
        if(dupContent == null)
    }
}
