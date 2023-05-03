package Project.OpenBook.Service;

import Project.OpenBook.Domain.Choice;
import Project.OpenBook.Domain.Topic;
import Project.OpenBook.Dto.choice.ChoiceDto;
import Project.OpenBook.Dto.choice.ChoiceAddDto;
import Project.OpenBook.Dto.choice.ChoiceUpdateDto;
import Project.OpenBook.Repository.topic.TopicRepository;
import Project.OpenBook.Repository.choice.ChoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChoiceService {

    private final ChoiceRepository choiceRepository;
    private final TopicRepository topicRepository;

    public List<ChoiceDto> queryTopicsByTopic(String topicTitle) {
        List<ChoiceDto> choiceDtoList = new ArrayList<>();
        List<Choice> choiceList = choiceRepository.queryChoiceByTopicTitle(topicTitle);
        choiceList.stream().forEach(c -> choiceDtoList.add(new ChoiceDto(c)));
        return choiceDtoList;
    }

    public ChoiceDto queryChoice(Long choiceId) {
        Choice choice = choiceRepository.findById(choiceId).orElse(null);
        if (choice == null) {
            return null;
        }

        return new ChoiceDto(choice);
    }

    public ChoiceDto queryRandomChoice(Long choiceId) {
        Choice choice = choiceRepository.queryRandChoiceByChoice(choiceId);
        if (choice == null) {
            return null;
        }
        return new ChoiceDto(choice);
    }
    public Boolean addChoices(ChoiceAddDto choiceAddDto) {

        Optional<Topic> topicOptional = topicRepository.findTopicByTitle(choiceAddDto.getTopicTitle());
        if (topicOptional.isEmpty()) {
            return false;
        }
        Topic topic = topicOptional.get();
        String[] contentArr = choiceAddDto.getChoiceArr();
        List<Choice> choiceList = Arrays.stream(contentArr).map(c -> new Choice(c, topic)).collect(Collectors.toList());
        choiceRepository.saveAll(choiceList);
        return true;
    }

    @Transactional
    public Choice updateChoice(ChoiceUpdateDto choiceUpdateDto, Long id) {
        Optional<Choice> choiceOptional = choiceRepository.findById(id);
       // Optional<Topic> topicOptional = topicRepository.findTopicByTitle(choiceUpdateDto.getTopic());
        if (choiceOptional.isEmpty() ) {
            return null;
        }
        Choice choice = choiceOptional.get();
        Choice updatedChoice = choice.updateContent(choiceUpdateDto.getContent());
        return updatedChoice;
    }

//    @Transactional
//    public Boolean updateChoices(ChoiceUpdateDto choiceUpdateDto) {
//        List<ChoiceContentIdDto> choiceContentIdDtoList = choiceUpdateDto.getChoiceList();
//        Map<Long, String> choiceMap = new ConcurrentHashMap<>();
//
//        //수정할 choiceList를 꺼내옴
//        List<Long> choiceIdList = choiceContentIdDtoList.stream().map(c -> c.getId()).collect(Collectors.toList());
//
//        //각 id, content를 매핑해놔서 수정할때 이용
//        choiceContentIdDtoList.stream().forEach(c -> choiceMap.put(c.getId(), c.getContent()));
//
//        //수정할 choiceList
//        List<Choice> choiceList = choiceRepository.queryChoicesById(choiceIdList);
//        //존재하지 않는 선지를 입력할 경우 false를 return
//        if (choiceList.size() != choiceIdList.size()) {
//            return false;
//        }
//        for (Choice choice : choiceList) {
//            choice.updateContent(choiceMap.get(choice.getId()));
//        }
//        return true;
//    }

//    public Boolean deleteChoices(List<Long> choiceIdList) {
//        List<Choice> choiceList = choiceRepository.queryChoicesById(choiceIdList);
//        if(choiceList.size() != choiceIdList.size()){
//            return false;
//        }
//
//        choiceRepository.deleteAllInBatch(choiceList);
//        return true;
//    }

    @Transactional
    public Boolean deleteChoice(Long choiceId) {
        Optional<Choice> choiceOptional = choiceRepository.findById(choiceId);
        if (choiceOptional.isEmpty()) {
            return false;
        }
        choiceRepository.deleteById(choiceId);
        return true;
    }

}
