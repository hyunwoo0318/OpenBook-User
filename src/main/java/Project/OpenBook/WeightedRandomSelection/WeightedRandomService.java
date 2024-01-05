package Project.OpenBook.WeightedRandomSelection;

import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import Project.OpenBook.WeightedRandomSelection.Model.KeywordSelectModel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class WeightedRandomService {


    public Keyword selectAnswerKeywords(List<KeywordSelectModel> answerKeywordSelectModelList) {
        WeightedRandomBag<Keyword> bag = new WeightedRandomBag<>();

        for (KeywordSelectModel model : answerKeywordSelectModelList) {
            if (model.getQuestionProb() != 0) {
                bag.addEntry(model.getKeyword(), model.getQuestionProb());
            }
        }

        return bag.getRandom();
    }

    public List<Keyword> selectWrongKeywords(List<KeywordSelectModel> keywordSelectModelList,
        int limit) {

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
            WeightedRandomBag<Keyword>.Entry randomEntry = bag.getRandomEntry();
            keywordSet.add(randomEntry.object);
            bag.removeEntry(randomEntry);
        }

        return new ArrayList<Keyword>(keywordSet);
    }


}
