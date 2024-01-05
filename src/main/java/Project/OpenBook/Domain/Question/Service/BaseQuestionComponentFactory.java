package Project.OpenBook.Domain.Question.Service;

import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import Project.OpenBook.Domain.Keyword.Repository.KeywordRepository;
import Project.OpenBook.Domain.LearningRecord.KeywordLearningRecord.Domain.KeywordLearningRecord;
import Project.OpenBook.Domain.QuestionCategory.Domain.QuestionCategory;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import Project.OpenBook.Domain.Topic.Repo.TopicRepository;
import Project.OpenBook.WeightedRandomSelection.Model.KeywordSelectModel;
import Project.OpenBook.WeightedRandomSelection.WeightedRandomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class BaseQuestionComponentFactory {

    private final TopicRepository topicRepository;
    private final KeywordRepository keywordRepository;
    private final WeightedRandomService weightedRandomService;


    /**
     * 해당 토픽을 제외한 Q.C가 같은 모든 키워드 리턴
     * 체크해야할 사항
     * 1. 정답 토픽과 토픽이 달라야함
     * 2. 정답 토픽에 존재하는 키워드와 내용이 일치하면 안됨
     *
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

    public List<Topic> getTopicsInQuestionCategories(List<QuestionCategory> questionCategoryList) {
        return topicRepository.queryTopicsInQuestionCategories(questionCategoryList);
    }


    public List<Topic> getWrongTopic(List<Topic> wrongTopicList, int limit) {
        List<Topic> topicList = new ArrayList<>();
        Set<Integer> randomIndexSet = getRandomIndex(limit, wrongTopicList.size());
        for (Integer i : randomIndexSet) {
            topicList.add(wrongTopicList.get(i));
        }
        return topicList;
    }

    public List<Keyword> getRandomOpenedKeywords(Topic answerTopic, Integer count) {
        return keywordRepository.queryRandomOpenedKeywords(answerTopic, count);
    }

    public List<Keyword> getRandomWrongKeywords(List<Keyword> keywordList) {
        return keywordRepository.queryKeywordsInQuestionCategories(keywordList);
    }

    public Keyword selectAnotherKeyword(Topic answerTopic, Keyword answerKeyword, List<Keyword> totalKeywordList) {
        Keyword anotherAnswerKeyword = null;
        List<Keyword> keywordList = totalKeywordList.stream()
                .filter(k -> {
                    if (k.getTopic() == answerTopic && k != answerKeyword) return true;
                    else return false;
                })
                .collect(Collectors.toList());
        if (keywordList.isEmpty()) return anotherAnswerKeyword;

        Set<Integer> randomIndexSet = getRandomIndex(1, keywordList.size());

        for (Integer i : randomIndexSet) {
            anotherAnswerKeyword = keywordList.get(i);
        }
        return anotherAnswerKeyword;
    }


    public List<Keyword> getTotalKeywordByAnswerTopic(String topicTitle) {
        return keywordRepository.queryKeywordsInTopic(topicTitle);
    }

    /**
     * 랜덤한 숫자를 고르는 로직
     *
     * @param count  골라야할 랜덤한 숫자 개수
     * @param maxNum 골라야할 랜덤한 숫자의 최대값 + 1
     * @return 랜덤한 숫자를 가지고 있는 집합
     */

    public Set<Integer> getRandomIndex(Integer count, Integer maxNum) {
        Set<Integer> ret = new HashSet<>();
        Random random = new Random();

        if (maxNum <= count) {
            for (int i = 0; i < maxNum; i++) {
                ret.add(i);
            }
            return ret;
        }

        while (ret.size() < count) {
            int randIdx = random.nextInt(maxNum);
            ret.add(randIdx);
        }
        return ret;
    }

    public Keyword selectAnswerKeyword(List<KeywordSelectModel> answerKeywordSelectModelList) {
        return weightedRandomService.selectAnswerKeywords(answerKeywordSelectModelList);
    }

    public List<KeywordSelectModel> makeAnswerKeywordSelectModelList(Map<Keyword, KeywordLearningRecord> keywordRecordMap,
                                                                     List<Keyword> totalKeywordList) {
        List<KeywordSelectModel> wrongKeywordModelList = new ArrayList<>();
        for (Keyword keyword : totalKeywordList) {
            KeywordLearningRecord record = keywordRecordMap.get(keyword);
            Integer questionProb = keyword.getQuestionProb();
            if (record != null) {
                if (questionProb - record.getAnswerCount() > 0) questionProb = questionProb - record.getAnswerCount();
                else questionProb = 0;
            }
            wrongKeywordModelList.add(new KeywordSelectModel(keyword, questionProb));
        }
        return wrongKeywordModelList;
    }


}
