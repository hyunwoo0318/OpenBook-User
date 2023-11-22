package Project.OpenBook.Domain.Question.Service;

import Project.OpenBook.Constants.QuestionConst;
import Project.OpenBook.Domain.Chapter.Domain.Chapter;
import Project.OpenBook.Domain.Chapter.Repo.ChapterRepository;
import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import Project.OpenBook.Domain.Keyword.KeywordPrimaryDate.Domain.KeywordPrimaryDate;
import Project.OpenBook.Domain.Keyword.KeywordPrimaryDate.Repository.KeywordPrimaryDateRepository;
import Project.OpenBook.Domain.Keyword.Repository.KeywordRepository;
import Project.OpenBook.Domain.LearningRecord.KeywordLearningRecord.Domain.KeywordLearningRecord;
import Project.OpenBook.Domain.LearningRecord.KeywordLearningRecord.Repo.KeywordLearningRecordRepository;
import Project.OpenBook.Domain.LearningRecord.QuestionCategoryLearningRecord.Repo.QuestionCategoryLearningRecordRepository;
import Project.OpenBook.Domain.LearningRecord.TopicLearningRecord.Repo.TopicLearningRecordRepository;
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
import Project.OpenBook.Handler.Exception.CustomException;
import Project.OpenBook.WeightedRandomSelection.WeightedRandomService;
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

    private QuestionCategoryLearningRecordRepository questionCategoryLearningRecordRepository;

    private WeightedRandomService weightedRandomService;

    private GetKeywordByTopicQuestion type1 ;
    private GetTopicByKeywordQuestion type2;

    public QuestionService(TopicRepository topicRepository, ChapterRepository chapterRepository, KeywordRepository keywordRepository,TimelineRepository timelineRepository,
                           QuestionCategoryRepository questionCategoryRepository, KeywordPrimaryDateRepository keywordPrimaryDateRepository, TopicPrimaryDateRepository topicPrimaryDateRepository,
                           TopicLearningRecordRepository topicLearningRecordRepository, KeywordLearningRecordRepository keywordLearningRecordRepository, WeightedRandomService weightedRandomService
                            ,QuestionCategoryLearningRecordRepository questionCategoryLearningRecordRepository){
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
        this.questionCategoryLearningRecordRepository = questionCategoryLearningRecordRepository;

        this.type1 = new GetKeywordByTopicQuestion(this.topicRepository, this.keywordRepository, this.weightedRandomService);
        this.type2 = new GetTopicByKeywordQuestion(this.topicRepository, this.keywordRepository, this.weightedRandomService);
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
            Topic topic = kp.getKeyword().getTopic();
            TimeFlowQuestionDto dto
                    = new TimeFlowQuestionDto(kp.getExtraDate(), kp.getExtraDateComment(),Arrays.asList(topic.getChapter().getTitle(), topic.getTitle()));
            timeFlowQuestionDtoList.add(dto);
        }

        for (TopicPrimaryDate tp : topicPrimaryDateList) {
            Chapter chapter = tp.getTopic().getChapter();
            List<String> keywordWithChapterList = new ArrayList<>();

            List<String> keywordList = tp.getTopic().getKeywordList().stream()
                    .sorted(Comparator.comparing(Keyword::getNumber))
                    .map(Keyword::getName)
                    .collect(Collectors.toList());

            keywordWithChapterList.add(chapter.getTitle());
            keywordWithChapterList.addAll(keywordList);
            TimeFlowQuestionDto dto
                    = new TimeFlowQuestionDto(tp.getExtraDate(), tp.getExtraDateComment(), keywordWithChapterList);
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
        List<Topic> topicList = topicRepository.queryTopicsWithKeywordList(num);
        if (topicList.isEmpty()) {
            throw new CustomException(CHAPTER_NOT_FOUND);
        }
        return type2.getJJHQuestion(topicList);
    }


    @Transactional
    public List<QuestionDto> queryRandomQuestion(Customer customer, Long questionCategoryId, Integer questionCount) {
        List<QuestionDto> questionList = new ArrayList<>();
        QuestionCategory questionCategory = null;
        if (questionCategoryId == -1L) {
            questionCategory = questionCategoryLearningRecordRepository.queryQuestionCategoryLowScore(customer).getQuestionCategory();
            questionCategoryId = questionCategory.getId();
        }else{
            questionCategory = questionCategoryRepository.queryQuestionCategoriesWithTopicList(questionCategoryId)
                    .orElseThrow(() -> {
                        throw new CustomException(QUESTION_CATEGORY_NOT_FOUND);
                    });
        }



        Map<Keyword, KeywordLearningRecord> keywordRecordMap = keywordLearningRecordRepository.queryKeywordLearningRecordsInQuestionCategory(customer, questionCategoryId).stream()
                .collect(Collectors.toMap(KeywordLearningRecord::getKeyword, k -> k));

        List<Keyword> totalKeywordList = new ArrayList<>(keywordRecordMap.keySet());


        List<QuestionDto> type1QuestionList = type1.getQuestion(keywordRecordMap, totalKeywordList, QuestionConst.GET_TOPIC_QUESTION_COUNT);
        List<QuestionDto> type2QuestionList = type2.getQuestion(keywordRecordMap, totalKeywordList, QuestionConst.GET_KEYWORD_QUESTION_COUNT);

        questionList.addAll(type1QuestionList);
        questionList.addAll(type2QuestionList);


        Collections.shuffle(questionList);
        return questionList;
    }
}

