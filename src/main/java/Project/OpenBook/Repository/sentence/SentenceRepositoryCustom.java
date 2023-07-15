package Project.OpenBook.Repository.sentence;

import Project.OpenBook.Domain.Sentence;

import java.util.List;

public interface SentenceRepositoryCustom {

    public List<Sentence> queryByTopicTitle(String topicTitle);
}
