package Project.OpenBook.Domain.StudyHistory.Service;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import Project.OpenBook.Domain.Keyword.Repository.KeywordRepository;
import Project.OpenBook.Domain.LearningRecord.KeywordLearningRecord.Domain.KeywordLearningRecord;
import Project.OpenBook.Domain.LearningRecord.KeywordLearningRecord.Repo.KeywordLearningRecordRepository;
import Project.OpenBook.Domain.LearningRecord.RoundLearningRecord.RoundLearningRecord;
import Project.OpenBook.Domain.LearningRecord.RoundLearningRecord.RoundLearningRecordRepository;
import Project.OpenBook.Domain.LearningRecord.TimelineLearningRecord.Domain.TimelineLearningRecord;
import Project.OpenBook.Domain.LearningRecord.TimelineLearningRecord.Repo.TimelineLearningRecordRepository;
import Project.OpenBook.Domain.LearningRecord.TopicLearningRecord.Domain.TopicLearningRecord;
import Project.OpenBook.Domain.LearningRecord.TopicLearningRecord.Repo.TopicLearningRecordRepository;
import Project.OpenBook.Domain.QuestionCategory.Domain.QuestionCategory;
import Project.OpenBook.Domain.QuestionCategoryLearningRecord.Domain.QuestionCategoryLearningRecord;
import Project.OpenBook.Domain.QuestionCategoryLearningRecord.Repo.QuestionCategoryLearningRecordRepository;
import Project.OpenBook.Domain.Round.Domain.Round;
import Project.OpenBook.Domain.Round.Repo.RoundRepository;
import Project.OpenBook.Domain.StudyHistory.Service.Dto.ExamQuestionScoreDto;
import Project.OpenBook.Domain.StudyHistory.Service.Dto.WrongCountAddDto;
import Project.OpenBook.Domain.Timeline.Domain.Timeline;
import Project.OpenBook.Domain.Timeline.Repo.TimelineRepository;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import Project.OpenBook.Handler.Exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static Project.OpenBook.Constants.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class StudyHistoryService {

    private final KeywordLearningRecordRepository keywordLearningRecordRepository;
    private final TopicLearningRecordRepository topicLearningRecordRepository;
    private final QuestionCategoryLearningRecordRepository questionCategoryLearningRecordRepository;
    private final TimelineLearningRecordRepository timelineLearningRecordRepository;
    private final RoundLearningRecordRepository roundLearningRecordRepository;

    private final KeywordRepository keywordRepository;
    private final TimelineRepository timelineRepository;
    private final RoundRepository roundRepository;


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
    public void saveTimelineWrongCount(Customer customer, WrongCountAddDto dto) {
        Long timelineId = dto.getId();
        Integer wrongCount = dto.getWrongCount();
        Integer answerCount = dto.getCorrectCount();

        Timeline timeline = timelineRepository.findById(timelineId).orElseThrow(() -> {
            throw new CustomException(TIMELINE_NOT_FOUND);
        });

        TimelineLearningRecord record = timelineLearningRecordRepository.findByCustomerAndTimeline(customer, timeline).orElseGet(() -> {
            TimelineLearningRecord newRecord = new TimelineLearningRecord(timeline, customer);
            timelineLearningRecordRepository.save(newRecord);
            return newRecord;
        });

        record.updateCount(answerCount, wrongCount);
    }

    @Transactional
    public void saveRoundWrongCount(Customer customer, ExamQuestionScoreDto dto) {
        Integer number = dto.getNumber();
        Integer score = dto.getScore();

        Round round = roundRepository.findRoundByNumber(number).orElseThrow(() -> {
            throw new CustomException(ROUND_NOT_FOUND);
        });

        RoundLearningRecord record = roundLearningRecordRepository.findByCustomerAndRound(customer, round).orElseGet(() -> {
            RoundLearningRecord newRecord = new RoundLearningRecord(round, customer);
            roundLearningRecordRepository.save(newRecord);
            return newRecord;
        });

        record.updateScore(score);
    }
}
