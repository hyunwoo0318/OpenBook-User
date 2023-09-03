package Project.OpenBook.Repository.topic;

import Project.OpenBook.Domain.PrimaryDate;
import Project.OpenBook.Domain.Topic;
import Project.OpenBook.Dto.topic.TopicAdminDto;
import Project.OpenBook.Dto.topic.TopicCustomerDto;
import com.querydsl.core.Tuple;
import com.querydsl.core.group.Group;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface  TopicRepositoryCustom {

    public Topic queryRandTopicByCategory(String categoryName);

    public Topic queryTopicByDescription(Long descriptionId);

    public Topic queryTopicByChoice(Long choiceId);


    public List<Tuple> queryAdminChapterDto(Integer chapterNum);

    public Map<String, Group> queryTopicAdminDto(String topicTitle);

    public Map<String, Group> queryTopicCustomerDto(String topicTitle);

    public Map<Topic, List<PrimaryDate>>  queryTimeFlowQuestion(Integer num);

    public Map<Topic, List<PrimaryDate>> queryTimeFlowQuestion();

    public List<String> queryTopicTitleInChapter(Integer num);

    public List<String> queryWrongTopicTitle(String topicTitle, int size);

    public Optional<Topic> queryTopicByNumber(Integer chapterNum, Integer topicNum);

    public List<String> queryTopicTitleCustomer(int num);
}
