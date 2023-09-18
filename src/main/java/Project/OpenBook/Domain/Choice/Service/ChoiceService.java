package Project.OpenBook.Domain.Choice.Service;

import Project.OpenBook.Constants.ChoiceType;
import Project.OpenBook.Domain.Choice.Domain.Choice;
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
import java.util.Map;

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

        String inputChoiceType = dto.getChoiceType();

        //입력받은 choiceType이 옳은 형식인지 확인
        ChoiceType choiceType = checkChoiceType(inputChoiceType);

        if(choiceType.equals(ChoiceType.String)){
            choice.updateChoice(dto.getChoice(), dto.getComment(), topic);
        }
        //선지 저장(이미지)
        else if(choiceType.equals(ChoiceType.Image)){
            String encodedFile = dto.getChoice();
            String choiceUrl = choice.getContent();
            if (!encodedFile.startsWith("https")){
                imageService.checkBase64(encodedFile);
                choiceUrl = imageService.storeFile(encodedFile);
            }
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

    private ChoiceType checkChoiceType(String inputChoiceType){
        //입력받은 choiceType이 옳은 형식인지 확인
        Map<String, ChoiceType> map = ChoiceType.getChoiceTypeNameMap();
        ChoiceType choiceType = map.get(inputChoiceType);
        if(choiceType == null){
            throw new CustomException(NOT_VALIDATE_CHOICE_TYPE);
        }
        return choiceType;
    }

}
