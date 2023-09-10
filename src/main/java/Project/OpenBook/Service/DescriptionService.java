package Project.OpenBook.Service;

import Project.OpenBook.Dto.choice.DupChoiceDto;
import Project.OpenBook.Utils.CustomException;
import Project.OpenBook.Domain.Description;
import Project.OpenBook.Topic.Domain.Topic;
import Project.OpenBook.Dto.description.DescriptionDto;
import Project.OpenBook.Repository.description.DescriptionRepository;
import Project.OpenBook.Topic.Repo.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static Project.OpenBook.Constants.ErrorCode.*;

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
        checkTopic(topicTitle);
        return descriptionRepository.findDescriptionsByTopic(topicTitle);
    }

//    @Transactional
//    public List<Description> addDescription(DescriptionCreateDto descriptionCreateDto) {
//        String topicTitle = descriptionCreateDto.getTopicTitle();
//        String[] contentList = descriptionCreateDto.getContentList();
//        dupDescription(contentList);
//        Topic topic = checkTopic(topicTitle);
//        List<Description> descriptionList = Arrays.stream(contentList).map(c -> new Description(c, topic)).collect(Collectors.toList());
//        descriptionRepository.saveAll(descriptionList);
//        return descriptionList;
//    }
//
//
//    @Transactional
//    public Description updateDescription(Long descriptionId, DescriptionUpdateDto descriptionUpdateDto) {
//
//        Description description = checkDescription(descriptionId);
//        String[] descriptionContentList = {descriptionUpdateDto.getContent()};
//        dupDescription(descriptionContentList);
//
//        Description updateDescription = description.updateContent(descriptionUpdateDto.getContent());
//        return updateDescription;
//    }
//
//    @Transactional
//    public boolean deleteDescription(Long descriptionId) {
//        checkDescription(descriptionId);
//        descriptionRepository.deleteById(descriptionId);
//        return true;
//    }

    private Description checkDescription(Long descriptionId) {
        return descriptionRepository.findById(descriptionId).orElseThrow(() ->{
            throw new CustomException(DESCRIPTION_NOT_FOUND);
        });
    }

    public List<DupChoiceDto> queryTopicDupChoices(Long descriptionId, String topicTitle) {
        checkDescription(descriptionId);
        checkTopic(topicTitle);

        return descriptionRepository.queryDupChoices(descriptionId, topicTitle);
    }

    private Topic checkTopic(String topicTitle) {
        return topicRepository.findTopicByTitle(topicTitle).orElseThrow(() -> {
            throw new CustomException(TOPIC_NOT_FOUND);
        });
    }

    private void dupDescription(String[] descriptionContentArr) {
        for (String content : descriptionContentArr) {
            if (descriptionRepository.queryDescriptionByContent(content) != null) {
                throw new CustomException(DUP_DESCRIPTION_CONTENT);
            }
        }
    }
}
