package Project.OpenBook.Domain.StudyHistory.Service;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.ExamQuestion.Domain.ExamQuestion;
import Project.OpenBook.Domain.ExamQuestion.Repo.ExamQuestionRepository;
import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import Project.OpenBook.Domain.Keyword.Repository.KeywordRepository;
import Project.OpenBook.Domain.LearningRecord.ExamQuestionLearningRecord.Domain.ExamQuestionLearningRecord;
import Project.OpenBook.Domain.LearningRecord.ExamQuestionLearningRecord.Repository.ExamQuestionLearningRecordRepository;
import Project.OpenBook.Domain.LearningRecord.KeywordLearningRecord.Domain.KeywordLearningRecord;
import Project.OpenBook.Domain.LearningRecord.KeywordLearningRecord.Repo.KeywordLearningRecordRepository;
import Project.OpenBook.Domain.LearningRecord.QuestionCategoryLearningRecord.Domain.QuestionCategoryLearningRecord;
import Project.OpenBook.Domain.LearningRecord.QuestionCategoryLearningRecord.Repo.QuestionCategoryLearningRecordRepository;
import Project.OpenBook.Domain.LearningRecord.RoundLearningRecord.RoundLearningRecord;
import Project.OpenBook.Domain.LearningRecord.RoundLearningRecord.RoundLearningRecordRepository;
import Project.OpenBook.Domain.LearningRecord.TimelineLearningRecord.Domain.TimelineLearningRecord;
import Project.OpenBook.Domain.LearningRecord.TimelineLearningRecord.Repo.TimelineLearningRecordRepository;
import Project.OpenBook.Domain.LearningRecord.TopicLearningRecord.Repo.TopicLearningRecordRepository;
import Project.OpenBook.Domain.QuestionCategory.Domain.QuestionCategory;
import Project.OpenBook.Domain.Round.Domain.Round;
import Project.OpenBook.Domain.Round.Repo.RoundRepository;
import Project.OpenBook.Domain.StudyHistory.Service.Dto.ExamQuestionRecordDto;
import Project.OpenBook.Domain.StudyHistory.Service.Dto.ExamQuestionScoreDto;
import Project.OpenBook.Domain.StudyHistory.Service.Dto.QuestionCategoryScoreAscentDto;
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
    private final ExamQuestionLearningRecordRepository examQuestionLearningRecordRepository;

    private final KeywordRepository keywordRepository;
    private final TimelineRepository timelineRepository;
    private final RoundRepository roundRepository;
    private final ExamQuestionRepository examQuestionRepository;


    @Transactional
    public List<QuestionCategoryScoreAscentDto> saveKeywordWrongCount(Customer customer, List<WrongCountAddDto> dtoList) {

        Map<QuestionCategory, Double> prevScoreMap = new HashMap<>();
        List<QuestionCategoryScoreAscentDto> returnDtoList = new ArrayList<>();

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
//        Map<Topic, TopicLearningRecord> topicRecordMap = topicLearningRecordRepository.queryTopicLearningRecordsInKeyword(customer, topicIdList)
//                .stream().collect(Collectors.toMap(tl -> tl.getTopic(), tl -> tl));
        Map<QuestionCategory, QuestionCategoryLearningRecord> qcRecordMap = questionCategoryLearningRecordRepository.queryQuestionRecordsInKeywords(customer, questionCategoryIdList).stream()
                .collect(Collectors.toMap(ql -> ql.getQuestionCategory(), ql -> ql));
        for (QuestionCategory questionCategory : qcRecordMap.keySet()) {
            QuestionCategoryLearningRecord record = qcRecordMap.get(questionCategory);
            prevScoreMap.put(questionCategory, (double)record.getAnswerCount());
        }


        for (WrongCountAddDto dto : dtoList) {
            Long keywordId = dto.getId();
            Integer wrongCount = dto.getWrongCount();
            Integer answerCount = dto.getCorrectCount();


            Keyword keyword = keywordMap.get(keywordId);
            if (keyword == null) {
                throw new CustomException(KEYWORD_NOT_FOUND);
            }

            Integer keywordQuestionProb = keyword.getQuestionProb();

            /**
             * 키워드 -> 토픽 -> q.c의 흐름으로 update하기
             */
            //1.키워드
            KeywordLearningRecord keywordLearningRecord = keywordRecordMap.get(keyword);
            if (keywordLearningRecord == null) {
                if(answerCount > keywordQuestionProb) answerCount = keywordQuestionProb;
                KeywordLearningRecord newRecord = new KeywordLearningRecord(keyword, customer,answerCount, wrongCount);
                keywordLearningRecordRepository.save(newRecord);
            }else{
                Integer prevAnswerCount = keywordLearningRecord.getAnswerCount();
                if(prevAnswerCount + answerCount > keywordQuestionProb) answerCount = keywordQuestionProb;
                keywordLearningRecord.updateCount(answerCount, wrongCount);
            }
//
//            //2. 토픽
//            Topic topic = keyword.getTopic();
//            TopicLearningRecord topicLearningRecord = topicRecordMap.get(topic);
//            if (topicLearningRecord == null) {
//                TopicLearningRecord newRecord = new TopicLearningRecord(topic, customer, answerCount, wrongCount);
//                topicLearningRecordRepository.save(newRecord);
//            }else{
//                topicLearningRecord.updateCount(answerCount, wrongCount);
//            }

            //3. q.c
            QuestionCategory questionCategory = keyword.getTopic().getQuestionCategory();
            QuestionCategoryLearningRecord qcLearningRecord = qcRecordMap.get(questionCategory);
            if (qcLearningRecord == null) {
                QuestionCategoryLearningRecord newRecord = new QuestionCategoryLearningRecord(questionCategory, customer, answerCount, wrongCount);
                questionCategoryLearningRecordRepository.save(newRecord);
            }else{
                qcLearningRecord.updateCount(answerCount, wrongCount);
            }


        }

        for (QuestionCategory questionCategory : qcRecordMap.keySet()) {
            Integer totalQuestionProb = questionCategory.getTotalQuestionProb();
            returnDtoList.add(new QuestionCategoryScoreAscentDto(questionCategory.getTitle(),
                    prevScoreMap.get(questionCategory) * 100/ (double)totalQuestionProb,
                    qcRecordMap.get(questionCategory).getAnswerCount() * 100 / (double)totalQuestionProb));
        }
        return returnDtoList;

    }

    @Transactional
    public void saveTimelineWrongCount(Customer customer, WrongCountAddDto dto) {
        Long timelineId = dto.getId();

        Timeline timeline = timelineRepository.findById(timelineId).orElseThrow(() -> {
            throw new CustomException(TIMELINE_NOT_FOUND);
        });

        TimelineLearningRecord record = timelineLearningRecordRepository.findByCustomerAndTimeline(customer, timeline).orElseGet(() -> {
            TimelineLearningRecord newRecord = new TimelineLearningRecord(timeline, customer);
            timelineLearningRecordRepository.save(newRecord);
            return newRecord;
        });

        record.updateScore();
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

    @Transactional
    public void saveQuestionWrongCount(Customer customer, List<ExamQuestionRecordDto> dtoList) {
        List<Long> examQuestionIdList = dtoList.stream()
                .map(dto -> dto.getId())
                .collect(Collectors.toList());
        Map<Long, ExamQuestionLearningRecord> questionRecordMap = examQuestionLearningRecordRepository.queryExamQuestionLearningRecords(customer, examQuestionIdList).stream()
                .collect(Collectors.toMap(record -> record.getExamQuestion().getId(), record -> record));
        Map<Round, RoundLearningRecord> roundRecordMap = roundLearningRecordRepository.queryRoundLearningRecord(customer).stream()
                .collect(Collectors.toMap(r -> r.getRound(), r -> r));

        for (ExamQuestionRecordDto dto : dtoList) {
            Long examQuestionId = dto.getId();
            Integer checkedChoiceKey = dto.getCheckedChoiceKey();
            Integer score = dto.getScore();

            ExamQuestionLearningRecord findRecord = questionRecordMap.get(examQuestionId);
            if (findRecord == null) {
                ExamQuestion examQuestion = examQuestionRepository.findById(examQuestionId).orElseThrow(() -> {
                    throw new CustomException(QUESTION_NOT_FOUND);
                });
                findRecord = new ExamQuestionLearningRecord(customer, examQuestion);
                examQuestionLearningRecordRepository.save(findRecord);
            }
            if(findRecord.getSolved()) continue;

            findRecord.updateInfo(checkedChoiceKey, score);
            Round round = findRecord.getExamQuestion().getRound();

            RoundLearningRecord roundRecord = roundRecordMap.get(round);
            roundRecord.updateScore(score);

            //틀린경우
            if (score == 0) {
                if (!findRecord.getIsRemovedAnswerNote() && !findRecord.getAnswerNoted()) {
                    findRecord.updateAnswerNoted(true);
                }
            }
        }
    }
}
