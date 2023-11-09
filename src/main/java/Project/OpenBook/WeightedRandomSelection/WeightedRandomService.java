package Project.OpenBook.WeightedRandomSelection;

import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import Project.OpenBook.WeightedRandomSelection.Model.KeywordSelectModel;
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


    public Keyword selectAnswerKeywords(List<KeywordSelectModel> answerKeywordSelectModelList) {
        WeightedRandomBag<Keyword> bag = new WeightedRandomBag<>();

        for (KeywordSelectModel model : answerKeywordSelectModelList) {
            if(model.getQuestionProb() != 0) bag.addEntry(model.getKeyword(), model.getQuestionProb());
        }

        return bag.getRandom();
    }

    public List<Keyword> selectWrongKeywords(List<KeywordSelectModel> keywordSelectModelList, int limit) {

        if (limit >= keywordSelectModelList.size()) {
            return keywordSelectModelList.stream()
                    .map(KeywordSelectModel::getKeyword)
                    .collect(Collectors.toList());
        }

        WeightedRandomBag<Keyword> bag = new WeightedRandomBag<>();

        for (KeywordSelectModel model : keywordSelectModelList) {
            bag.addEntry(model.getKeyword(), Double.valueOf(model.getQuestionProb()));
        }

        Set<Keyword> keywordSet = new HashSet<>();
        while (keywordSet.size() < limit) {
            //TODO : remove구현후 사용한 keyword는 제외하기
            keywordSet.add(bag.getRandom());
        }

        return new ArrayList<Keyword>(keywordSet);
    }


}
