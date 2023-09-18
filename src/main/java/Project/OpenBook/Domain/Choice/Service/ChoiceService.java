package Project.OpenBook.Domain.Choice.Service;

import Project.OpenBook.Constants.ChoiceConst;
import Project.OpenBook.Domain.Choice.Domain.Choice;
import Project.OpenBook.Domain.Choice.Dto.ChoiceDto;
import Project.OpenBook.Domain.Choice.Repository.ChoiceRepository;
import Project.OpenBook.Domain.ExamQuestion.Service.dto.ChoiceAddUpdateDto;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import Project.OpenBook.Domain.Topic.Repo.TopicRepository;
import Project.OpenBook.Handler.Exception.CustomException;
import Project.OpenBook.Image.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static Project.OpenBook.Constants.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class ChoiceService {

    private final ChoiceRepository choiceRepository;
    private final ImageService imageService;
    private final TopicRepository topicRepository;

    @Transactional
    public void updateChoice(Long choiceId, ChoiceAddUpdateDto dto) throws IOException {
        Choice choice = choiceRepository.findById(choiceId).orElseThrow(() -> {
            throw new CustomException(CHOICE_NOT_FOUND);
        });

        Topic topic = checkTopic(dto.getKey());

        String choiceType = dto.getChoiceType();

        if(choiceType.equals(ChoiceConst.CHOICE_STRING)){
            choice.updateChoice(dto.getChoice(), dto.getComment(), topic);
        }
        //선지 저장(이미지)
        else if(choiceType.equals(ChoiceConst.CHOICE_IMAGE)){
            String encodedFile = dto.getChoice();
            imageService.checkBase64(encodedFile);
            String choiceUrl = imageService.storeFile(encodedFile);
            choice.updateChoice(choiceUrl, dto.getComment(), topic);
        }else{
            throw new CustomException(NOT_VALIDATE_CHOICE_TYPE);
        }
    }

    @Transactional
    public void deleteChoice(Long choiceId) {
        Choice choice = choiceRepository.findById(choiceId).orElseThrow(() -> {
            throw new CustomException(CHOICE_NOT_FOUND);
        });

        choiceRepository.delete(choice);
    }

    private Topic checkTopic(String topicTitle) {
        return topicRepository.findTopicByTitle(topicTitle).orElseThrow(() -> {
            throw new CustomException(TOPIC_NOT_FOUND);
        });
    }
}
