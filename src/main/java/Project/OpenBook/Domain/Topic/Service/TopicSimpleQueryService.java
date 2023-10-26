package Project.OpenBook.Domain.Topic.Service;

import Project.OpenBook.Domain.Chapter.Service.dto.ChapterTopicUserDto;
import Project.OpenBook.Domain.Chapter.Service.dto.ChapterTopicWithCountDto;
import Project.OpenBook.Domain.Choice.Dto.ChoiceDto;
import Project.OpenBook.Domain.ChoiceComment.ChoiceKeyword.ChoiceKeyword;
import Project.OpenBook.Domain.ChoiceComment.ChoiceKeyword.ChoiceKeywordRepository;
import Project.OpenBook.Domain.Description.Dto.DescriptionDto;
import Project.OpenBook.Domain.Description.Repository.DescriptionKeywordRepository;
import Project.OpenBook.Domain.Description.Service.DescriptionKeyword;
import Project.OpenBook.Domain.ExamQuestion.Domain.ExamQuestion;
import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import Project.OpenBook.Domain.Keyword.Dto.KeywordDto;
import Project.OpenBook.Domain.Keyword.Dto.QuestionNumberDto;
import Project.OpenBook.Domain.Keyword.Repository.KeywordRepository;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import Project.OpenBook.Domain.Topic.Repo.TopicRepository;
import Project.OpenBook.Domain.Topic.Service.dto.PrimaryDateDto;
import Project.OpenBook.Domain.Topic.Service.dto.TopicDetailDto;
import Project.OpenBook.Domain.Topic.Service.dto.TopicWithKeywordDto;
import Project.OpenBook.Handler.Exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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

    public List<ChapterTopicWithCountDto> queryChapterTopicsAdmin(int num) {
        return topicRepository.queryTopicsWithQuestionCategory(num).stream()
                .sorted(Comparator.comparing(Topic::getNumber))
                .map(ChapterTopicWithCountDto::new)
                .collect(Collectors.toList());
    }


    public List<ChapterTopicUserDto> queryChapterTopicsCustomer(int num) {
        return topicRepository.queryTopicsWithQuestionCategory(num).stream()
                .sorted(Comparator.comparing(Topic::getNumber))
                .map(ChapterTopicUserDto::new)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public TopicDetailDto queryTopicsAdmin(String topicTitle) {

        Topic topic = topicRepository.queryTopicWithQuestionCategory(topicTitle).orElseThrow(() -> {
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

        List<KeywordDto> keywordDtoList = new ArrayList<>();

        Map<Keyword, List<DescriptionKeyword>> descriptionKeywordMap = descriptionKeywordRepository.queryDescriptionKeywordsAdmin(topicTitle).stream()
                .collect(Collectors.groupingBy(descriptionKeyword -> descriptionKeyword.getKeyword()));
        Map<Keyword, List<ChoiceKeyword>> choiceKeywordMap = choiceKeywordRepository.queryChoiceKeywordsAdmin(topicTitle).stream()
                .collect(Collectors.groupingBy(choiceKeyword -> choiceKeyword.getKeyword()));

        List<Keyword> keywordList = keywordRepository.queryKeywordsInTopicWithPrimaryDate(topicTitle);

        for (Keyword keyword : keywordList) {
            List<QuestionNumberDto> questionList = new ArrayList<>();
            List<DescriptionKeyword> descriptionKeywords = descriptionKeywordMap.get(keyword);
            List<ChoiceKeyword> choiceKeywords = choiceKeywordMap.get(keyword);
            if (descriptionKeywords != null) {
                for (DescriptionKeyword descriptionKeyword : descriptionKeywords) {
                    ExamQuestion examQuestion = descriptionKeyword.getDescription().getExamQuestion();
                    Integer roundNumber = examQuestion.getRound().getNumber();
                    Integer questionNumber = examQuestion.getNumber();
                    questionList.add(new QuestionNumberDto(roundNumber, questionNumber,null));
                }
            }

            if (choiceKeywords != null) {
                for (ChoiceKeyword choiceKeyword : choiceKeywords) {
                    ExamQuestion examQuestion = choiceKeyword.getChoice().getExamQuestion();
                    Integer roundNumber = examQuestion.getRound().getNumber();
                    Integer questionNumber = examQuestion.getNumber();
                    questionList.add(new QuestionNumberDto(roundNumber, questionNumber,null));
                }
            }

            List<PrimaryDateDto> primaryDateDtoList = keyword.getKeywordPrimaryDateList().stream()
                    .map(p -> new PrimaryDateDto(p.getExtraDate(), p.getExtraDateComment()))
                    .collect(Collectors.toList());
            KeywordDto keywordDto = new KeywordDto(keyword.getName(), keyword.getComment(), keyword.getImageUrl(), keyword.getId(),
                    keyword.getDateComment(), keyword.getNumber(), primaryDateDtoList, questionList);
            keywordDtoList.add(keywordDto);


        }

        return keywordDtoList;
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
