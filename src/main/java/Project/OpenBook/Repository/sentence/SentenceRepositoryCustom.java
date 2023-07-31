package Project.OpenBook.Repository.sentence;

import Project.OpenBook.Domain.Sentence;
import com.querydsl.core.Tuple;

import java.util.List;

public interface SentenceRepositoryCustom {

    public List<Sentence> queryByTopicTitle(String topicTitle);

    public List<Sentence> queryByTopicTitle(String topicTitle, int size);

    public List<Tuple> queryWrongSentences(String answerTopicTitle, int size);
}
