package Project.OpenBook.Repository.dupdate;

import Project.OpenBook.Domain.DupDate;
import Project.OpenBook.Domain.Topic;

import java.util.List;

public interface DupDateRepositoryCustom {

    public List<Topic> queryTopicsByDupDate(Integer startDate, Integer endDate);

    public List<DupDate> queryAllByTopic(String topicTitle);

    public Topic queryRandomAnswerTopic();

    public Topic queryRandomDescriptionTopic(Topic answerTopic);
}
