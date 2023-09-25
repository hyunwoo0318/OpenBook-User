package Project.OpenBook.Domain.Topic.Service;

import Project.OpenBook.Domain.Choice.Dto.ChoiceDto;
import Project.OpenBook.Domain.Description.Dto.DescriptionDto;
import Project.OpenBook.Domain.Sentence.Dto.SentenceDto;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import Project.OpenBook.Domain.Topic.Repo.TopicRepository;
import Project.OpenBook.Domain.Topic.Service.dto.TopicDetailDto;
import Project.OpenBook.Domain.Topic.Service.dto.TopicWithKeywordSentenceDto;
import Project.OpenBook.Domain.Keyword.Dto.KeywordDto;
import Project.OpenBook.Handler.Exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static Project.OpenBook.Constants.ErrorCode.TOPIC_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TopicSimpleQueryService {

    private final TopicRepository topicRepository;
    private final TopicValidator topicValidator;

    @Transactional(readOnly = true)
    public TopicDetailDto queryTopicsAdmin(String topicTitle) {
        Topic topic = topicRepository.queryTopicWithCategoryChapterEra(topicTitle).orElseThrow(() -> {
            throw new CustomException(TOPIC_NOT_FOUND);
        });
        return new TopicDetailDto(topic);
    }

    @Transactional(readOnly = true)
    public TopicWithKeywordSentenceDto queryTopicsCustomer(String topicTitle) {
        Topic topic = topicRepository.queryTopicWithCategory(topicTitle).orElseThrow(() -> {
            throw new CustomException(TOPIC_NOT_FOUND);
        });
        return new TopicWithKeywordSentenceDto(topic);
    }

    @Transactional(readOnly = true)
    public List<KeywordDto> queryTopicKeywords(String topicTitle) {
        return topicValidator.checkTopic(topicTitle).getKeywordList().stream()
                .map(k -> new KeywordDto(k.getName(), k.getComment(), k.getImageUrl(), k.getId()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SentenceDto> queryTopicSentences(String topicTitle) {
        return topicValidator.checkTopic(topicTitle).getSentenceList().stream()
                .map(s -> new SentenceDto(s.getName(), s.getId()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DescriptionDto> queryTopicDescriptions(String topicTitle) {
        return  topicValidator.checkTopic(topicTitle).getDescriptionList().stream()
                .map(d -> new DescriptionDto(d.getId(), d.getContent()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ChoiceDto> queryTopicChoices(String topicTitle) {
        return topicValidator.checkTopic(topicTitle).getChoiceList().stream()
                .map(c -> new ChoiceDto(c.getContent(), c.getId()))
                .collect(Collectors.toList());
    }
}
