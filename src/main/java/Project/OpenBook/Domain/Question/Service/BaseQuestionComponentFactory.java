package Project.OpenBook.Domain.Question.Service;

import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import Project.OpenBook.Domain.Keyword.Repository.KeywordRepository;
import Project.OpenBook.Domain.LearningRecord.KeywordLearningRecord.Domain.KeywordLearningRecord;
import Project.OpenBook.Domain.Question.Dto.QuizChoiceDto;
import Project.OpenBook.Domain.Question.Dto.QuizChoiceWithIdDto;
import Project.OpenBook.Domain.QuestionCategory.Domain.QuestionCategory;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import Project.OpenBook.Domain.Topic.Repo.TopicRepository;
import Project.OpenBook.Domain.LearningRecord.TopicLearningRecord.Domain.TopicLearningRecord;
import Project.OpenBook.WeightedRandomSelection.Model.TopicSelectModel;
import Project.OpenBook.WeightedRandomSelection.WeightedRandomService;
import Project.OpenBook.WeightedRandomSelection.Model.KeywordSelectModel;
import Project.OpenBook.Handler.Exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static Project.OpenBook.Constants.ErrorCode.TOPIC_NOT_FOUND;


@Component
@RequiredArgsConstructor
public class BaseQuestionComponentFactory {

    private final TopicRepository topicRepository;
    private final KeywordRepository keywordRepository;
    private final WeightedRandomService weightedRandomService;

    public List<Keyword> getKeywordsByAnswerTopic(String topicTitle, int limit) {
        return keywordRepository.queryKeywordsInTopicWithLimit(topicTitle, limit);
    }

    /**
     * 해당 토픽을 제외한 Q.C가 같은 모든 키워드 리턴
     * 체크해야할 사항
     *  1. 정답 토픽과 토픽이 달라야함
     *  2. 정답 토픽에 존재하는 키워드와 내용이 일치하면 안됨
     * @param topicTitle 정답 주제
     * @return
     */
    public List<Keyword> getTotalWrongKeywords(List<String> keywordNameList, String topicTitle) {
        return keywordRepository.queryWrongKeywords(keywordNameList, topicTitle);
    }

    public List<Keyword> getWrongKeywords(List<Keyword> totalWrongKeywordList, int limit) {
        //questionProb에 비례해서 limit만큼의 키워드를 선정 ( 키워드의 중복 X )
        List<KeywordSelectModel> modelList = totalWrongKeywordList.stream()
                .map(k -> new KeywordSelectModel(k, k.getQuestionProb()))
                .collect(Collectors.toList());
        return weightedRandomService.selectWrongKeywords(modelList, limit);
    }

    public List<Keyword> getKeywordInQuestionCategory(QuestionCategory questionCategory) {
        return keywordRepository.queryKeywordsInQuestionCategory(questionCategory);
    }



    public List<QuizChoiceDto> getWrongKeywordsByTopic(String topicTitle, int limit) {
        return keywordRepository.queryWrongKeywords(topicTitle, limit).stream()
                .map(k -> new QuizChoiceDto(k.getName(), k.getTopic().getTitle()))
                .collect(Collectors.toList());
    }


    public List<Topic> getWrongTopic(Topic answerTopic, int limit) {
        return topicRepository.queryWrongTopic(answerTopic,limit);
    }

    public List<QuizChoiceWithIdDto> toQuestionChoiceDtoByKeyword(List<Keyword> keywordList) {
        return keywordList.stream()
                .map(k -> new QuizChoiceWithIdDto(new QuizChoiceDto(k.getName(), k.getTopic().getTitle()), k.getId()))
                .collect(Collectors.toList());
    }


    public List<Keyword> getTotalKeywordByAnswerTopic(String topicTitle) {
        Topic topic = checkTopic(topicTitle);
        return topic.getKeywordList();
    }

    /**
     * 랜덤한 숫자를 고르는 로직
     * @param count 골라야할 랜덤한 숫자 개수
     * @param maxNum 골라야할 랜덤한 숫자의 최대값
     * @return 랜덤한 숫자를 가지고 있는 집합
     */

    public Set<Integer> getRandomIndex(Integer count, Integer maxNum) {
        Set<Integer> ret = new HashSet<>();
        Random random = new Random();

        while (ret.size() < count) {
            int randIdx = random.nextInt(maxNum);
            ret.add(randIdx);
        }
        return ret;
    }

    private Topic checkTopic(String topicTitle) {
        return topicRepository.findTopicByTitle(topicTitle).orElseThrow(() -> {
            throw new CustomException(TOPIC_NOT_FOUND);
        });
    }

//    public Topic selectAnswerTopic(List<TopicSelectModel> topicSelectModelList) {
//        return weightedRandomService.selectAnswerTopic(topicSelectModelList);
//    }

    public Keyword selectAnswerKeyword(List<KeywordSelectModel> answerKeywordSelectModelList) {
        return weightedRandomService.selectAnswerKeywords(answerKeywordSelectModelList);
    }

    public List<Keyword> selectWrongKeywordList(List<KeywordSelectModel> keywordSelectModelList, int count) {
        return weightedRandomService.selectWrongKeywords(keywordSelectModelList, count);
    }


    public List<TopicSelectModel> makeTopicSelectModelList(Map<Topic, TopicLearningRecord> topicRecordMap,QuestionCategory questionCategory) {
        List<Topic> topicList = questionCategory.getTopicList();
        List<TopicSelectModel> topicSelectModelList = new ArrayList<>();
        for (Topic topic : topicList) {
            TopicLearningRecord record = topicRecordMap.get(topic);
            Integer recordCount = 0;
            if (record != null) {
                recordCount = (record.getWrongCount());
            }
            topicSelectModelList.add(new TopicSelectModel(topic, recordCount + 1));
        }
        return topicSelectModelList;
    }

    public List<KeywordSelectModel> makeAnswerKeywordSelectModelList(Map<Keyword, KeywordLearningRecord> keywordRecordMap,
                                                                     List<Keyword> totalKeywordList) {
        List<KeywordSelectModel> wrongKeywordModelList = new ArrayList<>();
        for (Keyword keyword : totalKeywordList) {
            KeywordLearningRecord record = keywordRecordMap.get(keyword);
            Integer questionProb = keyword.getQuestionProb();
            if(record != null){
                if (questionProb - record.getAnswerCount() > 0) questionProb = questionProb - record.getAnswerCount();
                else questionProb = 0;
            }
            wrongKeywordModelList.add(new KeywordSelectModel(keyword, questionProb));
        }
        return wrongKeywordModelList;
    }

//    public List<KeywordSelectModel> makeWrongKeywordSelectModelList(Map<Keyword, KeywordLearningRecord> keywordRecordMap, Topic answerTopic, List<Keyword> answerKeywordList) {
//        List<Keyword> keywordList = keywordRepository.queryWrongKeywords(answerTopic.getTitle());
//        List<KeywordSelectModel> keywordSelectModelList = new ArrayList<>();
//
//        for (Keyword keyword : keywordList) {
//            KeywordSelectModel model = new KeywordSelectModel();
//            KeywordLearningRecord record = keywordRecordMap.get(keyword);
//
//            model.setKeyword(keyword);
//            model.setUsageCount(keyword.getQuestionProb() + 1);
//            if (record == null) {
//                model.setRecord(1);
//            }else{
//                model.setRecord(record.getWrongCount() + 1);
//            }
//            keywordSelectModelList.add(model);
//        }
//        return keywordSelectModelList;
//    }

}
