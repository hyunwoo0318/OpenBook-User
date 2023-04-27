package Project.OpenBook.Service;

import Project.OpenBook.Domain.Description;
import Project.OpenBook.Domain.Topic;
import Project.OpenBook.Dto.DescriptionCreateDto;
import Project.OpenBook.Dto.DescriptionDto;
import Project.OpenBook.Repository.description.DescriptionRepository;
import Project.OpenBook.Repository.topic.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

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

    @Transactional
    public Description addDescription(DescriptionCreateDto descriptionCreateDto) {
        String topicTitle = descriptionCreateDto.getTopicTitle();
        String content = descriptionCreateDto.getContent();

        Optional<Topic> topicOptional = topicRepository.findTopicByTitle(topicTitle);
        if (topicOptional.isEmpty()) {
            return null;
        }
        Topic topic = topicOptional.get();
        Description description = new Description(content, topic);
        descriptionRepository.save(description);
        return description;
    }


    @Transactional
    public Description updateDescription(Long descriptionId, DescriptionCreateDto descriptionCreateDto) {

        Optional<Description> descriptionOptional = descriptionRepository.findById(descriptionId);
        String topicTitle = descriptionCreateDto.getTopicTitle();
        Optional<Topic> topicOptional = topicRepository.findTopicByTitle(topicTitle);
        if (topicOptional.isEmpty() || descriptionOptional.isEmpty()) {
            return null;
        }

        Description description = descriptionOptional.get();
        Topic topic = topicOptional.get();

        Description updateDescription = description.updateDescription(descriptionCreateDto.getContent(), topic);
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
