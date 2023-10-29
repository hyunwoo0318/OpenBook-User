package Project.OpenBook.Domain.StudyHistory.Service;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import Project.OpenBook.Domain.Keyword.Repository.KeywordRepository;
import Project.OpenBook.Domain.KeywordLearningRecord.Domain.KeywordLearningRecord;
import Project.OpenBook.Domain.KeywordLearningRecord.Repo.KeywordLearningRecordRepository;
import Project.OpenBook.Domain.QuestionCategory.Domain.QuestionCategory;
import Project.OpenBook.Domain.QuestionCategoryLearningRecord.Domain.QuestionCategoryLearningRecord;
import Project.OpenBook.Domain.QuestionCategoryLearningRecord.Repo.QuestionCategoryLearningRecordRepository;
import Project.OpenBook.Domain.StudyHistory.Service.Dto.WrongCountAddDto;
import Project.OpenBook.Domain.Timeline.Domain.Timeline;
import Project.OpenBook.Domain.Timeline.Repo.TimelineRepository;
import Project.OpenBook.Domain.TimelineLearningRecord.Domain.TimelineLearningRecord;
import Project.OpenBook.Domain.TimelineLearningRecord.Repo.TimelineLearningRecordRepository;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import Project.OpenBook.Domain.TopicLearningRecord.Domain.TopicLearningRecord;
import Project.OpenBook.Domain.TopicLearningRecord.Repo.TopicLearningRecordRepository;
import Project.OpenBook.Handler.Exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static Project.OpenBook.Constants.ErrorCode.KEYWORD_NOT_FOUND;
import static Project.OpenBook.Constants.ErrorCode.TIMELINE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class StudyHistoryService {

    private final KeywordLearningRecordRepository keywordLearningRecordRepository;
    private final TopicLearningRecordRepository topicLearningRecordRepository;
    private final QuestionCategoryLearningRecordRepository questionCategoryLearningRecordRepository;
    private final TimelineLearningRecordRepository timelineLearningRecordRepository;

    private final KeywordRepository keywordRepository;
    private final TimelineRepository timelineRepository;


    @Transactional
    public void saveKeywordWrongCount(Customer customer, List<WrongCountAddDto> dtoList) {
        List<Long> keywordIdList = dtoList.stream()
                .map(d -> d.getId())
                .collect(Collectors.toList());

        Map<Long, Keyword> keywordMap = new HashMap<>();

        List<Long> topicIdList = new ArrayList<>();
        List<Long> questionCategoryIdList = new ArrayList<>();

        List<Keyword> keywordList = keywordRepository.queryKeywordsForUpdateHistory(keywordIdList);
        for (Keyword keyword : keywordList) {
            Long keywordId = keyword.getId();
            Topic topic = keyword.getTopic();
            QuestionCategory questionCategory = topic.getQuestionCategory();

            topicIdList.add(topic.getId());
            questionCategoryIdList.add(questionCategory.getId());

            keywordMap.put(keywordId, keyword);
        }

        Map<Keyword, KeywordLearningRecord> keywordRecordMap = keywordLearningRecordRepository.queryKeywordLearningRecordsInKeywords(customer, keywordIdList).stream()
                .collect(Collectors.toMap(kl -> kl.getKeyword(), kl -> kl));
        Map<Topic, TopicLearningRecord> topicRecordMap = topicLearningRecordRepository.queryTopicLearningRecordsInKeywords(customer, topicIdList)
                .stream().collect(Collectors.toMap(tl -> tl.getTopic(), tl -> tl));
        Map<QuestionCategory, QuestionCategoryLearningRecord> qcRecordMap = questionCategoryLearningRecordRepository.queryQuestionRecordsInKeywords(customer, questionCategoryIdList).stream()
                .collect(Collectors.toMap(ql -> ql.getQuestionCategory(), ql -> ql));


        for (WrongCountAddDto dto : dtoList) {
            Long keywordId = dto.getId();
            Integer wrongCount = dto.getWrongCount();
            Integer answerCount = dto.getCorrectCount();

            Keyword keyword = keywordMap.get(keywordId);
            if (keyword == null) {
                throw new CustomException(KEYWORD_NOT_FOUND);
            }

            /**
             * 키워드 -> 토픽 -> q.c의 흐름으로 update하기
             */
            //1.키워드
            KeywordLearningRecord keywordLearningRecord = keywordRecordMap.get(keyword);
            if (keywordLearningRecord == null) {
                KeywordLearningRecord newRecord = new KeywordLearningRecord(keyword, customer,answerCount, wrongCount);
                keywordLearningRecordRepository.save(newRecord);
            }else{
                keywordLearningRecord.updateCount(answerCount, wrongCount);
            }

            //2. 토픽
            Topic topic = keyword.getTopic();
            TopicLearningRecord topicLearningRecord = topicRecordMap.get(topic);
            if (topicLearningRecord == null) {
                TopicLearningRecord newRecord = new TopicLearningRecord(topic, customer, answerCount, wrongCount);
                topicLearningRecordRepository.save(newRecord);
            }else{
                topicLearningRecord.updateCount(answerCount, wrongCount);
            }

            //3. q.c
            QuestionCategory questionCategory = topic.getQuestionCategory();
            QuestionCategoryLearningRecord qcLearningRecord = qcRecordMap.get(questionCategory);
            if (qcLearningRecord == null) {
                QuestionCategoryLearningRecord newRecord = new QuestionCategoryLearningRecord(questionCategory, customer, answerCount, wrongCount);
                questionCategoryLearningRecordRepository.save(newRecord);
            }else{
                qcLearningRecord.updateCount(answerCount, wrongCount);
            }


        }

    }

    @Transactional
    public void saveTimelineWrongCount(Customer customer, List<WrongCountAddDto> dtoList) {
        List<Long> timelineIdList = dtoList.stream()
                .map(d -> d.getId())
                .collect(Collectors.toList());

        Map<Long, Timeline> timelineMap = timelineRepository.findAllById(timelineIdList).stream()
                .collect(Collectors.toMap(tl -> tl.getId(), tl -> tl));

        Map<Timeline, TimelineLearningRecord> timelineRecordMap = timelineLearningRecordRepository.queryTimelineLearningRecordInKeywords(customer, timelineIdList).stream()
                .collect(Collectors.toMap(tl -> tl.getTimeline(), tl -> tl));

        for (WrongCountAddDto dto : dtoList) {
            Long timelineId = dto.getId();
            Integer wrongCount = dto.getWrongCount();
            Integer answerCount = dto.getCorrectCount();

            Timeline timeline = timelineMap.get(timelineId);
            if (timeline == null) {
                throw new CustomException(TIMELINE_NOT_FOUND);
            }

            TimelineLearningRecord record = timelineRecordMap.get(timeline);
            if (record == null) {
                TimelineLearningRecord newRecord = new TimelineLearningRecord(timeline, customer, answerCount, wrongCount);
                timelineLearningRecordRepository.save(newRecord);
            }else{
                record.updateCount(answerCount, wrongCount);
            }

        }

    }
}
