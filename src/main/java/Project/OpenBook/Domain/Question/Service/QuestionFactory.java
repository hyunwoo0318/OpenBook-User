package Project.OpenBook.Domain.Question.Service;

import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import Project.OpenBook.Domain.LearningRecord.KeywordLearningRecord.Domain.KeywordLearningRecord;
import Project.OpenBook.Domain.Question.Dto.QuestionDto;
import Project.OpenBook.Domain.QuestionCategory.Domain.QuestionCategory;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import Project.OpenBook.Domain.LearningRecord.TopicLearningRecord.Domain.TopicLearningRecord;

import java.util.List;
import java.util.Map;

public interface QuestionFactory {
    public abstract  List<QuestionDto> getQuestion(Map<Topic, TopicLearningRecord> topicRecordMap, Map<Keyword, KeywordLearningRecord> keywordRecordMap,
                                         QuestionCategory questionCategory, Integer questionCount);

}
