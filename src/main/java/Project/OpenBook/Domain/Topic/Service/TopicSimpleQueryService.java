package Project.OpenBook.Domain.Topic.Service;

import Project.OpenBook.Domain.Choice.Dto.ChoiceDto;
import Project.OpenBook.Domain.ChoiceComment.ChoiceKeyword.ChoiceKeyword;
import Project.OpenBook.Domain.ChoiceComment.ChoiceKeyword.ChoiceKeywordRepository;
import Project.OpenBook.Domain.Description.Dto.DescriptionDto;
import Project.OpenBook.Domain.Description.Repository.DescriptionKeywordRepository;
import Project.OpenBook.Domain.Description.Service.DescriptionKeyword;
import Project.OpenBook.Domain.Keyword.Repository.KeywordRepository;
import Project.OpenBook.Domain.Search.TopicSearch.TopicSearch;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import Project.OpenBook.Domain.Topic.Repo.TopicRepository;
import Project.OpenBook.Domain.Search.TopicSearch.TopicSearchRepository;
import Project.OpenBook.Domain.Topic.Service.dto.PrimaryDateDto;
import Project.OpenBook.Domain.Topic.Service.dto.TopicDetailDto;
import Project.OpenBook.Domain.Topic.Service.dto.TopicWithKeywordDto;
import Project.OpenBook.Domain.Keyword.Dto.KeywordDto;
import Project.OpenBook.Handler.Exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static Project.OpenBook.Constants.ErrorCode.TOPIC_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TopicSimpleQueryService {

    private final TopicRepository topicRepository;
    private final KeywordRepository keywordRepository;
    private final DescriptionKeywordRepository descriptionKeywordRepository;
    private final ChoiceKeywordRepository choiceKeywordRepository;
    private final TopicValidator topicValidator;
    private final TopicSearchRepository topicSearchRepository;

    @Transactional(readOnly = true)
    public List<TopicSearch> searchEx(String input) {
       return topicSearchRepository.queryTopicSearchNameByInput(input);
    }


    @Transactional(readOnly = true)
    public TopicDetailDto queryTopicsAdmin(String topicTitle) {

        Topic topic = topicRepository.queryTopicWithCategoryChapterEra(topicTitle).orElseThrow(() -> {
            throw new CustomException(TOPIC_NOT_FOUND);
        });
        return new TopicDetailDto(topic);
    }

    @Transactional(readOnly = true)
    public TopicWithKeywordDto queryTopicsCustomer(String topicTitle) {
        Topic topic = topicRepository.queryTopicWithCategory(topicTitle).orElseThrow(() -> {
            throw new CustomException(TOPIC_NOT_FOUND);
        });
        return new TopicWithKeywordDto(topic);
    }

    @Transactional(readOnly = true)
    public List<KeywordDto> queryTopicKeywords(String topicTitle) {

        List<DescriptionKeyword> descriptionKeywordList = descriptionKeywordRepository.queryDescriptionKeywords(topicTitle);
        List<ChoiceKeyword> choiceKeywordList = choiceKeywordRepository.queryChoiceKeywords(topicTitle);

        return keywordRepository.queryKeywordsInTopicWithPrimaryDate(topicTitle)
                .stream()
                .map(k -> new KeywordDto(k.getName(), k.getComment(), k.getImageUrl(), k.getId(),
                        k.getDateComment(),k.getNumber(),
                        k.getKeywordPrimaryDateList().stream()
                                .map(p -> new PrimaryDateDto(p.getExtraDate(), p.getExtraDateComment()))
                                .collect(Collectors.toList()),
                        null))
                .sorted(Comparator.comparing(KeywordDto::getNumber))
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
