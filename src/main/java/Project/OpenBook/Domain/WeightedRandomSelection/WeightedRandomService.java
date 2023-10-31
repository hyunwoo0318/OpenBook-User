package Project.OpenBook.Domain.WeightedRandomSelection;

import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import Project.OpenBook.Domain.WeightedRandomSelection.Model.AnswerKeywordSelectModel;
import Project.OpenBook.Domain.WeightedRandomSelection.Model.TopicSelectModel;
import Project.OpenBook.Domain.WeightedRandomSelection.Model.WrongKeywordSelectModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class WeightedRandomService {

    private Double DEFAULT_WEIGHT = 500.0;
    private Double RECORD_WEIGHT_FOR_ANSWER_TOPIC = 0.1;
    private Double USAGE_WEIGHT_FOR_ANSWER_KEYWORD = 0.5;
    private Double RECORD_WEIGHT_FOR_ANSWER_KEYWORD = 0.5;

    private Double USAGE_WEIGHT_FOR_WRONG_KEYWORD = 0.4;
    private Double RECORD_WEIGHT_FOR_WRONG_KEYWORD = 0.3;
    private Double ASSOCIATION_WEIGHT_FOR_WRONG_KEYWORD = 0.3;

    public Topic selectAnswerTopic(List<TopicSelectModel> topicSelectModelList) {
        WeightedRandomBag<Topic> bag = new WeightedRandomBag<>();
        for (TopicSelectModel model : topicSelectModelList) {
            bag.addEntry(model.getTopic(), model.getRecord() * RECORD_WEIGHT_FOR_ANSWER_TOPIC + DEFAULT_WEIGHT);
        }
        return bag.getRandom();
    }

    public List<Keyword> selectAnswerKeywords(List<AnswerKeywordSelectModel> answerKeywordSelectModelList, int count) {

        if (count >= answerKeywordSelectModelList.size()) {
            return answerKeywordSelectModelList.stream()
                    .map(m -> m.getKeyword())
                    .collect(Collectors.toList());
        }
        WeightedRandomBag<Keyword> bag = new WeightedRandomBag<>();

        for (AnswerKeywordSelectModel model : answerKeywordSelectModelList) {
            Double totalScore = USAGE_WEIGHT_FOR_ANSWER_KEYWORD * model.getUsageCount()
                    + RECORD_WEIGHT_FOR_ANSWER_KEYWORD * model.getRecord()
                    + DEFAULT_WEIGHT;
            bag.addEntry(model.getKeyword(), totalScore);
        }

        Set<Keyword> keywordSet = new HashSet<>();
        while (keywordSet.size() < count) {
            keywordSet.add(bag.getRandom());
        }

        return new ArrayList<Keyword>(keywordSet);

    }

    public List<Keyword> selectWrongKeywords(List<WrongKeywordSelectModel> wrongKeywordSelectModelList, int count) {

        if (count >= wrongKeywordSelectModelList.size()) {
            return wrongKeywordSelectModelList.stream()
                    .map(m -> m.getKeyword())
                    .collect(Collectors.toList());
        }

        WeightedRandomBag<Keyword> bag = new WeightedRandomBag<>();

        for (WrongKeywordSelectModel model : wrongKeywordSelectModelList) {
            Double totalScore = USAGE_WEIGHT_FOR_WRONG_KEYWORD * model.getUsageCount()
                     + RECORD_WEIGHT_FOR_WRONG_KEYWORD * model.getRecord()
           //TODO        // + ASSOCIATION_WEIGHT_FOR_WRONG_KEYWORD * model.getAssociation()
                    + DEFAULT_WEIGHT;
            bag.addEntry(model.getKeyword(), totalScore);
        }

        Set<Keyword> keywordSet = new HashSet<>();
        while (keywordSet.size() < count) {
            keywordSet.add(bag.getRandom());
        }

        return new ArrayList<Keyword>(keywordSet);
    }

    public List<Topic> selectWrongTopics(List<TopicSelectModel> topicSelectModelList, int count) {

        if (count >= topicSelectModelList.size()) {
            return topicSelectModelList.stream()
                    .map(m -> m.getTopic())
                    .collect(Collectors.toList());
        }
        WeightedRandomBag<Topic> bag = new WeightedRandomBag<>();
        for (TopicSelectModel model : topicSelectModelList) {
            bag.addEntry(model.getTopic(), model.getRecord() * RECORD_WEIGHT_FOR_ANSWER_TOPIC
                                            + DEFAULT_WEIGHT);
        }

        Set<Topic> topicSet = new HashSet<>();
        while (topicSet.size() < count) {
            topicSet.add(bag.getRandom());
        }
        return new ArrayList<Topic>(topicSet);
    }

}
