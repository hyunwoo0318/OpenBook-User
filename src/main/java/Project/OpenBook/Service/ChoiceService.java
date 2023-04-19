package Project.OpenBook.Service;

import Project.OpenBook.Domain.Choice;
import Project.OpenBook.Domain.Topic;
import Project.OpenBook.Dto.ChoiceContentIdDto;
import Project.OpenBook.Dto.ChoiceDto;
import Project.OpenBook.Dto.ChoiceAddDto;
import Project.OpenBook.Dto.ChoiceUpdateDto;
import Project.OpenBook.Repository.TopicRepository;
import Project.OpenBook.Repository.choice.ChoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
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

    public Boolean updateChoices(ChoiceUpdateDto choiceUpdateDto) {
        List<ChoiceContentIdDto> choiceContentIdDtoList = choiceUpdateDto.getChoiceContentIdDtoList();
        Map<Long, String> choiceMap = new ConcurrentHashMap<>();

        //수정할 choiceList를 꺼내옴
        List<Long> choiceIdList = choiceContentIdDtoList.stream().map(c -> c.getId()).collect(Collectors.toList());
        if(choiceIdList.size() != choiceContentIdDtoList.size()){
            return false;
        }

        //각 id, content를 매핑해놔서 수정할때 이용
        choiceContentIdDtoList.stream().forEach(c -> choiceMap.put(c.getId(), c.getContent()));

        //수정할 choiceList
        List<Choice> choiceList = choiceRepository.queryChoicesById(choiceIdList);
        for (Choice choice : choiceList) {
            choice.updateContent(choiceMap.get(choice.getId()));
        }
        return true;
    }

    public Boolean deleteChoices(List<Long> choiceIdList) {
        List<Choice> choiceList = choiceRepository.queryChoicesById(choiceIdList);
        if(choiceList.size() != choiceIdList.size()){
            return false;
        }

        choiceRepository.deleteAllInBatch(choiceList);
        return true;
    }
}