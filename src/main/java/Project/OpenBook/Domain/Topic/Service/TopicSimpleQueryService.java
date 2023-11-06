package Project.OpenBook.Domain.Topic.Service;

import Project.OpenBook.Domain.Chapter.Domain.Chapter;
import Project.OpenBook.Domain.Chapter.Service.dto.ChapterTopicWithCountDto;
import Project.OpenBook.Domain.Choice.Domain.Choice;
import Project.OpenBook.Domain.ChoiceComment.ChoiceKeyword.ChoiceKeyword;
import Project.OpenBook.Domain.ChoiceComment.ChoiceKeyword.ChoiceKeywordRepository;
import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.DescriptionComment.DescriptionKeyword.DescriptionKeyword;
import Project.OpenBook.Domain.DescriptionComment.DescriptionKeyword.DescriptionKeywordRepository;
import Project.OpenBook.Domain.ExamQuestion.Domain.ExamQuestion;
import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import Project.OpenBook.Domain.Keyword.Dto.KeywordDto;
import Project.OpenBook.Domain.Keyword.Dto.QuestionNumberDto;
import Project.OpenBook.Domain.Keyword.Repository.KeywordRepository;
import Project.OpenBook.Domain.LearningRecord.TopicLearningRecord.Domain.TopicLearningRecord;
import Project.OpenBook.Domain.LearningRecord.TopicLearningRecord.Repo.TopicLearningRecordRepository;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import Project.OpenBook.Domain.Topic.Repo.TopicRepository;
import Project.OpenBook.Domain.Topic.Service.dto.*;
import Project.OpenBook.Handler.Exception.CustomException;
import lombok.AllArgsConstructor;
import lombok.Getter;
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

    private final TopicLearningRecordRepository topicLearningRecordRepository;

    public List<ChapterTopicWithCountDto> queryChapterTopicsAdmin(int num) {
        return topicRepository.queryTopicsWithQuestionCategory(num).stream()
                .sorted(Comparator.comparing(Topic::getNumber))
                .map(ChapterTopicWithCountDto::new)
                .collect(Collectors.toList());
    }

    public List<TopicListQueryDto> queryChapterTopicsCustomer(int chapterNum) {
        List<Topic> topicList = topicRepository.queryTopicsWithCategory(chapterNum);


        return getTopicListQueryDtoList(topicList, chapterNum);
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

        MapSet mapSet = makeMapSet(topicTitle);
        Map<Keyword, List<DescriptionKeyword>> descriptionKeywordMap = mapSet.getDescriptionKeywordMap();
        Map<Keyword, List<ChoiceKeyword>> choiceKeywordMap = mapSet.getChoiceKeywordMap();

        List<Keyword> keywordList = keywordRepository.queryKeywordsInTopicWithPrimaryDate(topicTitle);

        List<KeywordDto> keywordDtoList = makeKeywordDtoList(keywordList, descriptionKeywordMap, choiceKeywordMap);

        return new TopicWithKeywordDto(topic, keywordDtoList);
    }

    @Transactional(readOnly = true)
    public List<KeywordDto> queryTopicKeywords(String topicTitle) {
        MapSet mapSet = makeMapSet(topicTitle);
        Map<Keyword, List<DescriptionKeyword>> descriptionKeywordMap = mapSet.getDescriptionKeywordMap();
        Map<Keyword, List<ChoiceKeyword>> choiceKeywordMap = mapSet.getChoiceKeywordMap();

        List<Keyword> keywordList = keywordRepository.queryKeywordsInTopicWithPrimaryDate(topicTitle);

        List<KeywordDto> keywordDtoList = makeKeywordDtoList(keywordList, descriptionKeywordMap, choiceKeywordMap);
        return keywordDtoList;
    }

    @Transactional(readOnly = true)
    public List<TopicQueryInQuestionCategoryDto> queryTopicsInQuestionCategory(Long id) {
        return topicRepository.queryTopicsInQuestionCategory(id).stream()
                .map(TopicQueryInQuestionCategoryDto::new)
                .collect(Collectors.toList());
    }



    public List<BookmarkedTopicQueryDto> queryBookmarkedTopics(Customer customer) {
        List<BookmarkedTopicQueryDto> dtoList = new ArrayList<>();
        List<TopicLearningRecord> recordList = topicLearningRecordRepository.queryTopicLearningRecordsBookmarked(customer);
        Map<Chapter, List<TopicLearningRecord>> chapterTopicRecordMap = recordList.stream()
                .collect(Collectors.groupingBy(r -> r.getTopic().getChapter()));

        for (Chapter chapter : chapterTopicRecordMap.keySet()) {
            List<Topic> topicList = chapterTopicRecordMap.get(chapter).stream()
                    .map(TopicLearningRecord::getTopic)
                    .collect(Collectors.toList());

            List<TopicListQueryDto> topicDtoList = getTopicListQueryDtoList(topicList);
            BookmarkedTopicQueryDto dto = new BookmarkedTopicQueryDto(chapter.getTitle(), topicDtoList);
            dtoList.add(dto);
        }
        return dtoList;
    }

    private List<TopicListQueryDto> getTopicListQueryDtoList(List<Topic> topicList, int chapterNum) {
        MapSet mapSet = makeMapSet(chapterNum);
        Map<Keyword, List<DescriptionKeyword>> descriptionKeywordMap = mapSet.getDescriptionKeywordMap();
        Map<Keyword, List<ChoiceKeyword>> choiceKeywordMap = mapSet.getChoiceKeywordMap();
        Map<Topic, List<Keyword>> topicKeywordMap = mapSet.getTopicKeywordMap();

        List<TopicListQueryDto> topicDtoList = new ArrayList<>();
        for (Topic topic : topicList) {
            List<Keyword> keywordList = topicKeywordMap.get(topic);
            List<KeywordDto> keywordDtoList = new ArrayList<>();
            if (keywordList != null) {
                keywordDtoList = makeKeywordDtoList(keywordList, descriptionKeywordMap, choiceKeywordMap);
            }
            TopicListQueryDto dto = new TopicListQueryDto(topic, keywordDtoList);
            topicDtoList.add(dto);
        }
        return topicDtoList;
    }

    private List<TopicListQueryDto> getTopicListQueryDtoList(List<Topic> topicList) {

        MapSet mapSet = makeMapSet(topicList);
        Map<Keyword, List<DescriptionKeyword>> descriptionKeywordMap = mapSet.getDescriptionKeywordMap();
        Map<Keyword, List<ChoiceKeyword>> choiceKeywordMap = mapSet.getChoiceKeywordMap();
        Map<Topic, List<Keyword>> topicKeywordMap = mapSet.getTopicKeywordMap();

        List<TopicListQueryDto> topicDtoList = new ArrayList<>();
        for (Topic topic : topicList) {
            List<Keyword> keywordList = topicKeywordMap.get(topic);
            List<KeywordDto> keywordDtoList = new ArrayList<>();
            if (keywordList != null) {
                keywordDtoList = makeKeywordDtoList(keywordList, descriptionKeywordMap, choiceKeywordMap);
            }
            TopicListQueryDto dto = new TopicListQueryDto(topic, keywordDtoList);
            topicDtoList.add(dto);
        }
        return topicDtoList;
    }

    private List<KeywordDto> makeKeywordDtoList(List<Keyword> keywordList, Map<Keyword, List<DescriptionKeyword>> descriptionKeywordMap,
                                                Map<Keyword, List<ChoiceKeyword>> choiceKeywordMap) {
        List<KeywordDto> keywordDtoList = new ArrayList<>();
        for (Keyword keyword : keywordList) {
            List<QuestionNumberDto> questionList = new ArrayList<>();
            List<DescriptionKeyword> descriptionKeywords = descriptionKeywordMap.get(keyword);
            List<ChoiceKeyword> choiceKeywords = choiceKeywordMap.get(keyword);
            /**
             * 선지 / 보기에서 키워드 사용 여부 확인
             */
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
                    Choice choice = choiceKeyword.getChoice();
                    ExamQuestion examQuestion = choice.getExamQuestion();
                    Integer roundNumber = examQuestion.getRound().getNumber();
                    Integer questionNumber = examQuestion.getNumber();
                    questionList.add(new QuestionNumberDto(roundNumber, questionNumber,choice.getContent()));
                }
            }

            /**
             * keywordPrimaryDate 쿼리
             */
            List<PrimaryDateDto> primaryDateDtoList = keyword.getKeywordPrimaryDateList().stream()
                    .map(p -> new PrimaryDateDto(p.getExtraDate(), p.getExtraDateComment()))
                    .collect(Collectors.toList());

            KeywordDto keywordDto = new KeywordDto(keyword.getName(), keyword.getComment(), keyword.getImageUrl(), keyword.getId(),
                    keyword.getDateComment(), keyword.getNumber(), primaryDateDtoList, questionList);
            keywordDtoList.add(keywordDto);
        }
        return keywordDtoList;
    }

    private MapSet makeMapSet(String topicTitle) {
        Map<Keyword, List<DescriptionKeyword>> descriptionKeywordMap = descriptionKeywordRepository.queryDescriptionKeywordsForTopicList(topicTitle).stream()
                .collect(Collectors.groupingBy(DescriptionKeyword::getKeyword));
        Map<Keyword, List<ChoiceKeyword>> choiceKeywordMap = choiceKeywordRepository.queryChoiceKeywordsForTopicList(topicTitle).stream()
                .collect(Collectors.groupingBy(ChoiceKeyword::getKeyword));
        return new MapSet(descriptionKeywordMap, choiceKeywordMap, null);
    }

    private MapSet makeMapSet(int chapterNum) {
        Map<Keyword, List<DescriptionKeyword>> descriptionKeywordMap = descriptionKeywordRepository.queryDescriptionKeywordsForTopicList(chapterNum).stream()
                .collect(Collectors.groupingBy(dk -> dk.getKeyword()));
        Map<Keyword, List<ChoiceKeyword>> choiceKeywordMap = choiceKeywordRepository.queryChoiceKeywordsForTopicList(chapterNum).stream()
                .collect(Collectors.groupingBy(ck -> ck.getKeyword()));
        Map<Topic, List<Keyword>> topicKeywordMap = keywordRepository.queryKeywordsInTopicWithPrimaryDate(chapterNum).stream()
                .collect(Collectors.groupingBy(Keyword::getTopic));
        return new MapSet(descriptionKeywordMap, choiceKeywordMap, topicKeywordMap);
    }

    private MapSet makeMapSet(List<Topic> topicList) {
        Map<Keyword, List<DescriptionKeyword>> descriptionKeywordMap = descriptionKeywordRepository.queryDescriptionKeywordsForTopicList(topicList).stream()
                .collect(Collectors.groupingBy(DescriptionKeyword::getKeyword));
        Map<Keyword, List<ChoiceKeyword>> choiceKeywordMap = choiceKeywordRepository.queryChoiceKeywordsForTopicList(topicList).stream()
                .collect(Collectors.groupingBy(ChoiceKeyword::getKeyword));
        Map<Topic, List<Keyword>> topicKeywordMap = keywordRepository.queryKeywordsInTopicWithPrimaryDate(topicList).stream()
                .collect(Collectors.groupingBy(Keyword::getTopic));
        return new MapSet(descriptionKeywordMap, choiceKeywordMap, topicKeywordMap);
    }

    @Getter
    @AllArgsConstructor
    private class MapSet {
        Map<Keyword, List<DescriptionKeyword>> descriptionKeywordMap;
        Map<Keyword, List<ChoiceKeyword>> choiceKeywordMap;
        Map<Topic, List<Keyword>> topicKeywordMap;
    }
}
