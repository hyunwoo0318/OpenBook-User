package Project.OpenBook.Service;

import Project.OpenBook.Utils.CustomException;
import Project.OpenBook.Domain.Choice;
import Project.OpenBook.Domain.Description;
import Project.OpenBook.Domain.DupContent;
import Project.OpenBook.Dto.choice.ChoiceDto;
import Project.OpenBook.Dto.choice.ChoiceIdListDto;
import Project.OpenBook.Repository.dupcontent.DupContentRepository;
import Project.OpenBook.Repository.choice.ChoiceRepository;
import Project.OpenBook.Repository.description.DescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static Project.OpenBook.Constants.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class DupContentService {

    private final DupContentRepository dupContentRepository;
    private final DescriptionRepository descriptionRepository;

    private final ChoiceRepository choiceRepository;

    public List<ChoiceDto> queryDupContentChoices(Long descriptionId) {
        checkDescription(descriptionId);
        List<ChoiceDto> choiceDtoList = new ArrayList<>();
        List<Choice> choiceList = dupContentRepository.queryDupContentChoices(descriptionId);
        choiceList.stream().forEach(c -> choiceDtoList.add(new ChoiceDto(c)));
        return choiceDtoList;
    }

    @Transactional
    public List<DupContent> addDupContentChoices(Long descriptionId, ChoiceIdListDto choiceIdListDto) {
        List<Long> choiceIdList = choiceIdListDto.getChoiceIdList();
        Description description = checkDescription(descriptionId);

        List<Choice> choiceList = choiceRepository.queryChoicesById(choiceIdList);
        if(choiceList.size() != choiceIdList.size()){
            throw new CustomException(CHOICE_NOT_FOUND);
        }

        //해당 보기와 내용이 겹치다고 이미 저장해놓은 선지 id 집합 -> 해당 집합에 새로 받은 id가 존재하면 따로 저장하지않음.
        Set<Long> dupContentIdSet = dupContentRepository.queryDupContentChoices(descriptionId).stream().map(c -> c.getId()).collect(Collectors.toSet());
        List<DupContent> dupContentList = new ArrayList<>();
        for (Choice choice : choiceList) {
            if(dupContentIdSet.contains(choice.getId())){
                continue;
            }
            DupContent dupContent = new DupContent(description, choice);
            dupContentList.add(dupContent);
        }

        dupContentRepository.saveAll(dupContentList);
        return dupContentList;
    }

    public void deleteDupContentChoices(Long descriptionId, Long choiceId) {
        checkChoice(choiceId);
        checkChoice(descriptionId);

        DupContent dupContent = dupContentRepository.queryDupContent(descriptionId, choiceId);
        if (dupContent == null) {
            throw new CustomException(NOT_SAVED_CHOICE);
        }
        dupContentRepository.delete(dupContent);
    }

    private Choice checkChoice(Long choiceId) {
        return choiceRepository.findById(choiceId).orElseThrow(() ->{
            throw new CustomException(CHOICE_NOT_FOUND);
        });
    }

    private Description checkDescription(Long descriptionId) {
        return descriptionRepository.findById(descriptionId).orElseThrow(() ->{
            throw new CustomException(DESCRIPTION_NOT_FOUND);
        });
    }

}
