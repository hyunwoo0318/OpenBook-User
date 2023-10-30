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
import Project.OpenBook.Domain.WeightedRandomSelection.AnswerKeywordSelectModel;
import Project.OpenBook.Domain.WeightedRandomSelection.TopicSelectModel;
import Project.OpenBook.Domain.WeightedRandomSelection.WeightedRandomService;
import Project.OpenBook.Domain.WeightedRandomSelection.WrongKeywordSelectModel;
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
     * @param topicTitle 정답 주제
     * @return
     */
    public List<Keyword> getTotalWrongKeywords(String topicTitle) {
        return keywordRepository.queryWrongKeywords(topicTitle);
    }



    public List<QuizChoiceDto> getWrongKeywordsByTopic(String topicTitle, int limit) {
        return keywordRepository.queryWrongKeywords(topicTitle, limit).stream()
                .map(k -> new QuizChoiceDto(k.getName(), k.getTopic().getTitle()))
                .collect(Collectors.toList());
    }


    public List<QuizChoiceDto> getWrongTopic(String answerTopicTitle, int limit) {
        return topicRepository.queryWrongTopicTitle(answerTopicTitle,limit).stream()
                .map(t -> QuizChoiceDto.builder()
                        .key(t)
                        .choice(t)
                        .build())
                .collect(Collectors.toList());
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

    public Topic selectAnswerTopic(List<TopicSelectModel> topicSelectModelList) {
        return weightedRandomService.selectAnswerTopic(topicSelectModelList);
    }

    public List<Keyword> selectAnswerKeywordList(List<AnswerKeywordSelectModel> answerKeywordSelectModelList, int count) {
        return weightedRandomService.selectAnswerKeywords(answerKeywordSelectModelList, count);
    }

    public List<Keyword> selectWrongKeywordList(List<WrongKeywordSelectModel> wrongKeywordSelectModelList, int count) {
        return weightedRandomService.selectWrongKeywords(wrongKeywordSelectModelList, count);
    }

    public List<Topic> selectWrongTopicList(List<TopicSelectModel> topicSelectModelList, int count) {
        return weightedRandomService.selectWrongTopics(topicSelectModelList, count);
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

    public List<AnswerKeywordSelectModel> makeAnswerKeywordSelectModelList(Map<Keyword, KeywordLearningRecord> keywordRecordMap, Topic topic) {
        List<Keyword> keywordList = topic.getKeywordList();
        List<AnswerKeywordSelectModel> answerKeywordSelectModelList = new ArrayList<>();
        for (Keyword keyword : keywordList) {
            KeywordLearningRecord record = keywordRecordMap.get(keyword);
            AnswerKeywordSelectModel model = null;
            if (record == null) {
                model = new AnswerKeywordSelectModel(keyword, 1, 1);
            }else{
                model = new AnswerKeywordSelectModel(keyword, record.getWrongCount() + 1, keyword.getUsageCount() + 1);
            }
            answerKeywordSelectModelList.add(model);
        }
        return answerKeywordSelectModelList;
    }

    public List<WrongKeywordSelectModel> makeWrongKeywordSelectModelList(Map<Keyword, KeywordLearningRecord> keywordRecordMap, Topic answerTopic, List<Keyword> answerKeywordList) {
        List<Keyword> keywordList = keywordRepository.queryWrongKeywords(answerTopic.getTitle());
        List<WrongKeywordSelectModel> wrongKeywordSelectModelList = new ArrayList<>();

        for (Keyword keyword : keywordList) {
            WrongKeywordSelectModel model = new WrongKeywordSelectModel();
            KeywordLearningRecord record = keywordRecordMap.get(keyword);

            model.setKeyword(keyword);
            model.setUsageCount(keyword.getUsageCount() + 1);
            if (record == null) {
                model.setRecord(1);
            }else{
                model.setRecord(record.getWrongCount() + 1);
            }
            wrongKeywordSelectModelList.add(model);
        }
        return wrongKeywordSelectModelList;
    }

}
