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
import lombok.AllArgsConstructor;
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

    private GetKeywordByTopicQuestion type1;
    private GetTopicByKeywordQuestion type2;

    private final Long RANDOM_INDEX = 0L;
    private final Long TOTAL_INDEX = -1L;

    public QuestionService(TopicRepository topicRepository, ChapterRepository chapterRepository, KeywordRepository keywordRepository, TimelineRepository timelineRepository,
                           QuestionCategoryRepository questionCategoryRepository, KeywordPrimaryDateRepository keywordPrimaryDateRepository, TopicPrimaryDateRepository topicPrimaryDateRepository,
                           TopicLearningRecordRepository topicLearningRecordRepository, KeywordLearningRecordRepository keywordLearningRecordRepository, WeightedRandomService weightedRandomService
            , QuestionCategoryLearningRecordRepository questionCategoryLearningRecordRepository) {
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

        // primaryDate 쿼리
        PrimaryDateQueryResult result = getPrimaryDateQueryResult(timelineId);

        //쿼리문 결과를 dto로 변환
        keywordPrimaryDateToQuestionDto(result, timeFlowQuestionDtoList);
        topicPrimaryDateToQuestionDto(result, timeFlowQuestionDtoList);

        //정렬
        Collections.sort(timeFlowQuestionDtoList, Comparator.comparing(TimeFlowQuestionDto::getDate));

        return timeFlowQuestionDtoList;
    }

    /**
     * TopicPrimaryDate를 TimeFlowQuestionDto로 변환해주는 메서드
     *
     * @param result
     * @param timeFlowQuestionDtoList
     */
    private static void topicPrimaryDateToQuestionDto(PrimaryDateQueryResult result, List<TimeFlowQuestionDto> timeFlowQuestionDtoList) {
        for (TopicPrimaryDate tp : result.topicPrimaryDateList) {
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
    }

    /**
     * KeywordPrimaryDate를 TimeFlowQuestionDto로 변환해주는 메서드
     *
     * @param result
     * @param timeFlowQuestionDtoList
     */
    private static void keywordPrimaryDateToQuestionDto(PrimaryDateQueryResult result, List<TimeFlowQuestionDto> timeFlowQuestionDtoList) {
        for (KeywordPrimaryDate kp : result.keywordPrimaryDateList) {
            Topic topic = kp.getKeyword().getTopic();
            String keywordComment = kp.getKeyword().getComment();
            List<String> commentList = new ArrayList<>();
            commentList.add(topic.getChapter().getTitle() + " - " + topic.getTitle());
            if (!keywordComment.isBlank()) {
                List<String> splitCommentList = Arrays.stream(keywordComment.split("[.]")).collect(Collectors.toList());
                commentList.addAll(splitCommentList);
            }
            TimeFlowQuestionDto dto
                    = new TimeFlowQuestionDto(kp.getExtraDate(), kp.getExtraDateComment(), commentList);


            timeFlowQuestionDtoList.add(dto);
        }
    }

    /**
     * timelineId에 맞게 primaryDate를 쿼리하는 로직
     * timelineId == RANDOM_INDEX -> 전체 timeline중 랜덤하게 1개를 선정해 해당 timeline에서 primaryDate 조회
     * timelineId == TOTAL_INDEX -> 전체 timeline의 모든 primaryDate조회
     *
     * @param timelineId
     * @return
     */
    private PrimaryDateQueryResult getPrimaryDateQueryResult(Long timelineId) {
        List<KeywordPrimaryDate> keywordPrimaryDateList;
        List<TopicPrimaryDate> topicPrimaryDateList;
        if (timelineId == TOTAL_INDEX) {
            topicPrimaryDateList = topicPrimaryDateRepository.queryTopicPrimaryDateInTimeline(TOTAL_INDEX, 0, 0);
            keywordPrimaryDateList = keywordPrimaryDateRepository.queryKeywordPrimaryDateInTimeline(TOTAL_INDEX, 0, 0);

        } else {
            if (timelineId == RANDOM_INDEX) {
                timelineId = timelineRepository.queryRandomTimeline().orElseThrow(() -> {
                    throw new CustomException(TIMELINE_NOT_FOUND);
                });
            }

            Timeline timeline = timelineRepository.queryTimelineWithEra(timelineId).orElseThrow(() -> {
                throw new CustomException(TIMELINE_NOT_FOUND);
            });
            Long eraId = timeline.getEra().getId();
            Integer startDate = timeline.getStartDate();
            Integer endDate = timeline.getEndDate();


            topicPrimaryDateList = topicPrimaryDateRepository.queryTopicPrimaryDateInTimeline(eraId, startDate, endDate);
            keywordPrimaryDateList = keywordPrimaryDateRepository.queryKeywordPrimaryDateInTimeline(eraId, startDate, endDate);
        }
        return new PrimaryDateQueryResult(topicPrimaryDateList, keywordPrimaryDateList);
    }

    @AllArgsConstructor
    private static class PrimaryDateQueryResult {
        public final List<TopicPrimaryDate> topicPrimaryDateList;
        public final List<KeywordPrimaryDate> keywordPrimaryDateList;
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
    public List<QuestionDto> queryRandomQuestion(Customer customer, Long questionCategoryId) {
        List<QuestionDto> questionList = new ArrayList<>();
        QuestionCategory questionCategory = null;
        if (questionCategoryId == -1L) {
            questionCategory = questionCategoryLearningRecordRepository.queryQuestionCategoryLowScore(customer).getQuestionCategory();
            questionCategoryId = questionCategory.getId();
        } else {
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

