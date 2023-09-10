package Project.OpenBook.Repository.dupdate;

import Project.OpenBook.Domain.DupDate;
import Project.OpenBook.Topic.Domain.Topic;

import java.util.List;

public interface DupDateRepositoryCustom {

    public List<Topic> queryAnswerTopics(Integer startDate, Integer endDate);
    public List<Topic> queryDescriptionTopics(Integer startDate, Integer endDate);

    public List<DupDate> queryAllByTopic(String topicTitle);

    public Topic queryRandomAnswerTopic();

    public Topic queryRandomDescriptionTopic(Topic answerTopic);
}
