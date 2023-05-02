package Project.OpenBook.Service;

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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DescriptionService {

    private final DescriptionRepository descriptionRepository;
    private final TopicRepository topicRepository;


    public DescriptionDto queryDescription(Long descriptionId) {

        Optional<Description> descriptionOptional = descriptionRepository.findById(descriptionId);
        if (descriptionOptional.isEmpty()) {
            return null;
        }
        Description description = descriptionOptional.get();

        DescriptionDto descriptionDto = new DescriptionDto(description);
        return descriptionDto;
    }


    public List<Description> queryDescriptionsInTopic(String topicTitle) {
        if(topicRepository.findTopicByTitle(topicTitle).isEmpty()){
            return null;
        }
        return descriptionRepository.findDescriptionsByTopic(topicTitle);
    }

    @Transactional
    public List<Description> addDescription(DescriptionCreateDto descriptionCreateDto) {
        String topicTitle = descriptionCreateDto.getTopicTitle();
        String[] contentList = descriptionCreateDto.getContentList();

        Optional<Topic> topicOptional = topicRepository.findTopicByTitle(topicTitle);
        if (topicOptional.isEmpty()) {
            return null;
        }
        Topic topic = topicOptional.get();
        List<Description> descriptionList = Arrays.stream(contentList).map(c -> new Description(c, topic)).collect(Collectors.toList());
        descriptionRepository.saveAll(descriptionList);
        return descriptionList;
    }


    @Transactional
    public Description updateDescription(Long descriptionId, DescriptionUpdateDto descriptionUpdateDto) {

        Optional<Description> descriptionOptional = descriptionRepository.findById(descriptionId);
        if (descriptionOptional.isEmpty()) {
            return null;
        }

        Description description = descriptionOptional.get();

        Description updateDescription = description.updateContent(descriptionUpdateDto.getContent());
        return updateDescription;
    }

    @Transactional
    public boolean deleteDescription(Long descriptionId) {
        Optional<Description> descriptionOptional = descriptionRepository.findById(descriptionId);
        if (descriptionOptional.isEmpty()) {
            return false;
        }
        descriptionRepository.deleteById(descriptionId);
        return true;
    }

}
