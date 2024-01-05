package Project.OpenBook.Domain.Question.Service;

import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import Project.OpenBook.Domain.LearningRecord.KeywordLearningRecord.Domain.KeywordLearningRecord;
import Project.OpenBook.Domain.Question.Dto.QuestionDto;

import java.util.List;
import java.util.Map;

public interface QuestionFactory {
    public abstract List<QuestionDto> getQuestion(Map<Keyword, KeywordLearningRecord> keywordRecordMap,
                                                  List<Keyword> totalKeywordList, Integer questionCount);

}
