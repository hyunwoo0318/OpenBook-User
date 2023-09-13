package Project.OpenBook.Domain.Sentence.Repository;

import Project.OpenBook.Domain.Sentence.Domain.Sentence;
import com.querydsl.core.Tuple;

import java.util.List;
import java.util.Optional;

public interface SentenceRepositoryCustom {

    public Optional<Sentence> querySentenceByContentInTopic(String name, String topicTitle);

    public List<Sentence> queryByTopicTitle(String topicTitle);

    public List<Sentence> queryByTopicTitle(String topicTitle, int limit);

    public List<Tuple> queryWrongSentences(String answerTopicTitle, int limit);
}
