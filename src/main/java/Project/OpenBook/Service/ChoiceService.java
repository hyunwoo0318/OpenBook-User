package Project.OpenBook.Service;

import Project.OpenBook.Utils.CustomException;
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

import static Project.OpenBook.Constants.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class ChoiceService {

    private final ChoiceRepository choiceRepository;
    private final TopicRepository topicRepository;

    public List<ChoiceDto> queryChoicesByTopic(String topicTitle) {
        List<Choice> choiceList = choiceRepository.queryChoiceByTopicTitle(topicTitle);
        List<ChoiceDto> choiceDtoList = choiceList.stream().map(c -> new ChoiceDto(c)).collect(Collectors.toList());
        return choiceDtoList;
    }



    public ChoiceDto queryChoice(Long choiceId) {
        Choice choice = checkChoice(choiceId);

        return new ChoiceDto(choice);
    }

    public ChoiceDto queryRandomChoice(Long choiceId) {
        Choice prevChoice = checkChoice(choiceId);

        Choice choice = choiceRepository.queryRandChoiceByChoice(choiceId);
        if (choice == null) {
            return new ChoiceDto(prevChoice);
        }
        return new ChoiceDto(choice);
    }
//    public void addChoices(ChoiceAddDto choiceAddDto) {
//
//        Topic topic = topicRepository.findTopicByTitle(choiceAddDto.getTopicTitle()).orElseThrow(() -> {
//            throw new CustomException(TOPIC_NOT_FOUND);
//        });
//
//        String[] contentArr = choiceAddDto.getChoiceArr();
//        dupChoice(contentArr);
//        List<Choice> choiceList = Arrays.stream(contentArr).map(c -> new Choice(c, topic)).collect(Collectors.toList());
//        choiceRepository.saveAll(choiceList);
//    }
//
//    @Transactional
//    public Choice updateChoice(ChoiceUpdateDto choiceUpdateDto, Long choiceId) {
//        Choice choice = checkChoice(choiceId);
//        String[] choiceContentArr = {choiceUpdateDto.getContent()};
//        dupChoice(choiceContentArr);
//        Choice updatedChoice = choice.updateContent(choiceUpdateDto.getContent());
//        return updatedChoice;
//    }
//
//
//    @Transactional
//    public Boolean deleteChoice(Long choiceId) {
//        checkChoice(choiceId);
//        choiceRepository.deleteById(choiceId);
//        return true;
//    }

    private Choice checkChoice(Long choiceId) {
        return choiceRepository.findById(choiceId).orElseThrow(() ->{
            throw new CustomException(CHOICE_NOT_FOUND);
        });
    }

    private void dupChoice(String[] choiceContentArr) {
        for (String content : choiceContentArr) {
            if (choiceRepository.queryChoiceByContent(content) != null) {
                throw new CustomException(DUP_CHOICE_CONTENT);
            }
        }
    }


}
