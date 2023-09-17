package Project.OpenBook.Domain.Sentence.Repository;

import Project.OpenBook.Domain.Sentence.Domain.Sentence;
import com.querydsl.core.Tuple;

import java.util.List;
import java.util.Optional;

public interface SentenceRepositoryCustom {

    public List<Sentence> queryByTopicTitle(String topicTitle, int limit);

    public List<Sentence> queryWrongSentences(String answerTopicTitle, int limit);
}
