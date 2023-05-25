package Project.OpenBook.Service;

import Project.OpenBook.CustomException;
import Project.OpenBook.Domain.Description;
import Project.OpenBook.Domain.Topic;
import Project.OpenBook.Dto.description.DescriptionCreateDto;
import Project.OpenBook.Dto.description.DescriptionDto;
import Project.OpenBook.Dto.description.DescriptionUpdateDto;
import Project.OpenBook.Repository.description.DescriptionRepository;
import Project.OpenBook.Repository.topic.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static Project.OpenBook.Constants.ErrorCode.DESCRIPTION_NOT_FOUND;
import static Project.OpenBook.Constants.ErrorCode.TOPIC_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class DescriptionService {

    private final DescriptionRepository descriptionRepository;
    private final TopicRepository topicRepository;


    public DescriptionDto queryDescription(Long descriptionId) {

        Description description = checkDescription(descriptionId);

        DescriptionDto descriptionDto = new DescriptionDto(description);
        return descriptionDto;
    }

    public DescriptionDto queryRandomDescription(Long descriptionId) {
        Description prevDescription = checkDescription(descriptionId);
        Description description = descriptionRepository.queryRandDescriptionByDescription(descriptionId);
        if (description == null) {
            return new DescriptionDto(prevDescription);
        }
        return new DescriptionDto(description);
    }


    public List<Description> queryDescriptionsInTopic(String topicTitle) {
        topicRepository.findTopicByTitle(topicTitle).orElseThrow(() -> {
            throw new CustomException(TOPIC_NOT_FOUND);
        });
        return descriptionRepository.findDescriptionsByTopic(topicTitle);
    }

    @Transactional
    public List<Description> addDescription(DescriptionCreateDto descriptionCreateDto) {
        String topicTitle = descriptionCreateDto.getTopicTitle();
        String[] contentList = descriptionCreateDto.getContentList();

        Topic topic = topicRepository.findTopicByTitle(topicTitle).orElseThrow(() -> {
            throw new CustomException(TOPIC_NOT_FOUND);
        });
        List<Description> descriptionList = Arrays.stream(contentList).map(c -> new Description(c, topic)).collect(Collectors.toList());
        descriptionRepository.saveAll(descriptionList);
        return descriptionList;
    }


    @Transactional
    public Description updateDescription(Long descriptionId, DescriptionUpdateDto descriptionUpdateDto) {

        Description description = checkDescription(descriptionId);

        Description updateDescription = description.updateContent(descriptionUpdateDto.getContent());
        return updateDescription;
    }

    @Transactional
    public boolean deleteDescription(Long descriptionId) {
        checkDescription(descriptionId);
        descriptionRepository.deleteById(descriptionId);
        return true;
    }

    private Description checkDescription(Long descriptionId) {
        return descriptionRepository.findById(descriptionId).orElseThrow(() ->{
            throw new CustomException(DESCRIPTION_NOT_FOUND);
        });
    }
}
