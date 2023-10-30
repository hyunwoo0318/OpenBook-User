package Project.OpenBook.Domain.Question.Service;

import Project.OpenBook.Domain.Chapter.Domain.Chapter;
import Project.OpenBook.Domain.Chapter.Repo.ChapterRepository;
import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import Project.OpenBook.Domain.Keyword.KeywordPrimaryDate.Domain.KeywordPrimaryDate;
import Project.OpenBook.Domain.Keyword.KeywordPrimaryDate.Repository.KeywordPrimaryDateRepository;
import Project.OpenBook.Domain.Keyword.Repository.KeywordRepository;
import Project.OpenBook.Domain.LearningRecord.KeywordLearningRecord.Domain.KeywordLearningRecord;
import Project.OpenBook.Domain.LearningRecord.KeywordLearningRecord.Repo.KeywordLearningRecordRepository;
import Project.OpenBook.Domain.Question.Dto.QuestionDto;
import Project.OpenBook.Domain.Question.Dto.TimeFlowQuestionDto;
import Project.OpenBook.Domain.QuestionCategory.Domain.QuestionCategory;
import Project.OpenBook.Domain.QuestionCategory.Repo.QuestionCategoryRepository;
import Project.OpenBook.Domain.Timeline.Domain.Timeline;
import Project.OpenBook.Domain.Timeline.Repo.TimelineRepository;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import Project.OpenBook.Domain.Topic.Repo.TopicRepository;
import Project.OpenBook.Domain.Topic.TopicPrimaryDate.Domain.TopicPrimaryDate;
import Project.OpenBook.Domain.Topic.TopicPrimaryDate.Repository.TopicPrimaryDateRepository;
import Project.OpenBook.Domain.LearningRecord.TopicLearningRecord.Domain.TopicLearningRecord;
import Project.OpenBook.Domain.LearningRecord.TopicLearningRecord.Repo.TopicLearningRecordRepository;
import Project.OpenBook.Domain.WeightedRandomSelection.WeightedRandomService;
import Project.OpenBook.Handler.Exception.CustomException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

import static Project.OpenBook.Constants.ErrorCode.*;

@Service
public class QuestionService {

    private TopicRepository topicRepository;
    private ChapterRepository chapterRepository;
    private KeywordRepository keywordRepository;
    private TimelineRepository timelineRepository;
    private QuestionCategoryRepository questionCategoryRepository;
    private KeywordPrimaryDateRepository keywordPrimaryDateRepository;
    private TopicPrimaryDateRepository topicPrimaryDateRepository;

    private TopicLearningRecordRepository topicLearningRecordRepository;
    private KeywordLearningRecordRepository keywordLearningRecordRepository;

    private WeightedRandomService weightedRandomService;

    private GetKeywordByTopicQuestion type1 ;
    private GetTopicByKeywordQuestion type2;

    public QuestionService(TopicRepository topicRepository, ChapterRepository chapterRepository, KeywordRepository keywordRepository,TimelineRepository timelineRepository,
                           QuestionCategoryRepository questionCategoryRepository, KeywordPrimaryDateRepository keywordPrimaryDateRepository, TopicPrimaryDateRepository topicPrimaryDateRepository,
                           TopicLearningRecordRepository topicLearningRecordRepository, KeywordLearningRecordRepository keywordLearningRecordRepository, WeightedRandomService weightedRandomService) {
        this.topicRepository = topicRepository;
        this.chapterRepository = chapterRepository;
        this.keywordRepository = keywordRepository;
        this.topicPrimaryDateRepository = topicPrimaryDateRepository;
        this.keywordPrimaryDateRepository = keywordPrimaryDateRepository;
        this.timelineRepository = timelineRepository;
        this.questionCategoryRepository = questionCategoryRepository;
        this.topicLearningRecordRepository = topicLearningRecordRepository;
        this.keywordLearningRecordRepository = keywordLearningRecordRepository;
        this.weightedRandomService = weightedRandomService;

        this.type1 = new GetKeywordByTopicQuestion(this.topicRepository, this.keywordRepository, this.weightedRandomService);
        this.type2 = new GetTopicByKeywordQuestion(this.topicRepository, this.keywordRepository, this.weightedRandomService);
    }

    private Topic checkTopic(String topicTitle) {
        return topicRepository.findTopicByTitle(topicTitle).orElseThrow(() -> {
            throw new CustomException(TOPIC_NOT_FOUND);
        });
    }


    @Transactional
    public List<TimeFlowQuestionDto> queryTimeFlowQuestion(Long timelineId) {

        List<TimeFlowQuestionDto> timeFlowQuestionDtoList = new ArrayList<>();
        List<TopicPrimaryDate> topicPrimaryDateList = new ArrayList<>();
        List<KeywordPrimaryDate> keywordPrimaryDateList = new ArrayList<>();
        if (timelineId == 0) {
            timelineId = timelineRepository.queryRandomTimeline().orElseThrow(() -> {
                throw new CustomException(TIMELINE_NOT_FOUND);
            });
        }
        if (timelineId == -1) {
            topicPrimaryDateList = topicPrimaryDateRepository.queryTopicPrimaryDateInTimeline(-1L, 0, 0);
            keywordPrimaryDateList = keywordPrimaryDateRepository.queryKeywordPrimaryDateInTimeline(-1L, 0, 0);
        } else{
            Timeline timeline = timelineRepository.queryTimelineWithEra(timelineId).orElseThrow(() -> {
                throw new CustomException(TIMELINE_NOT_FOUND);
            });
            Long eraId = timeline.getEra().getId();
            Integer startDate = timeline.getStartDate();
            Integer endDate = timeline.getEndDate();


            topicPrimaryDateList = topicPrimaryDateRepository.queryTopicPrimaryDateInTimeline(eraId, startDate, endDate);
            keywordPrimaryDateList = keywordPrimaryDateRepository.queryKeywordPrimaryDateInTimeline(eraId, startDate, endDate);
        }

        for (KeywordPrimaryDate kp : keywordPrimaryDateList) {
            TimeFlowQuestionDto dto
                    = new TimeFlowQuestionDto(kp.getExtraDate(), kp.getExtraDateComment(), kp.getKeyword().getName(),null);
            timeFlowQuestionDtoList.add(dto);
        }

        for (TopicPrimaryDate tp : topicPrimaryDateList) {
            List<String> keywordList = tp.getTopic().getKeywordList().stream()
                    .sorted(Comparator.comparing(Keyword::getNumber))
                    .map(Keyword::getName)
                    .collect(Collectors.toList());
            TimeFlowQuestionDto dto
                    = new TimeFlowQuestionDto(tp.getExtraDate(), tp.getExtraDateComment(), tp.getTopic().getTitle(), keywordList);
            timeFlowQuestionDtoList.add(dto);
        }

        //연도 순으로 오름차순으로 정렬
        Collections.sort(timeFlowQuestionDtoList, Comparator.comparing(TimeFlowQuestionDto::getDate));

        return timeFlowQuestionDtoList;
    }


    @Transactional
    public List<QuestionDto> queryGetKeywordsQuestion(String topicTitle) {
        return type1.getJJHQuestion(topicTitle);
    }


    @Transactional
    public List<QuestionDto> queryGetTopicsByKeywordQuestion(Integer num) {
        List<QuestionDto> questionList = new ArrayList<>();

        Chapter chapter = chapterRepository.findOneByNumber(num).orElseThrow(() -> {
            throw new CustomException(CHAPTER_NOT_FOUND);
        });

        List<String> topicTitleList = chapter.getTopicList().stream()
                .map(Topic::getTitle)
                .collect(Collectors.toList());
        for (String topicTitle : topicTitleList) {
            QuestionDto dto = type2.getJJHQuestion(topicTitle);
            if (dto != null) {
                questionList.add(dto);
            }
        }

        return questionList;
    }


    @Transactional
    public List<QuestionDto> queryRandomQuestion(Customer customer, Long questionCategoryId, Integer questionCount) {
        List<QuestionDto> questionList = new ArrayList<>();

        QuestionCategory questionCategory = questionCategoryRepository.queryQuestionCategoriesWithTopicList(questionCategoryId)
                .orElseThrow(() -> {
                    throw new CustomException(QUESTION_CATEGORY_NOT_FOUND);
                });

        Map<Topic, TopicLearningRecord> topicRecordMap = topicLearningRecordRepository.queryTopicLearningRecordsInQuestionCategory(customer, questionCategoryId).stream()
                .collect(Collectors.toMap(t -> t.getTopic(), t -> t));
        Map<Keyword, KeywordLearningRecord> keywordRecordMap = keywordLearningRecordRepository.queryKeywordLearningRecordsInQuestionCategory(customer, questionCategoryId).stream()
                .collect(Collectors.toMap(k -> k.getKeyword(), k -> k));


        Integer count1, count2 = 0;
        count1 = questionCount / 2;
        count2 = questionCount - count1;
        List<QuestionDto> type1QuestionList = type1.getQuestion(topicRecordMap, keywordRecordMap, questionCategory, count1);
        List<QuestionDto> type2QuestionList = type2.getQuestion(topicRecordMap, keywordRecordMap, questionCategory, count2);

        questionList.addAll(type1QuestionList);
        questionList.addAll(type2QuestionList);


        return questionList;
    }
}

