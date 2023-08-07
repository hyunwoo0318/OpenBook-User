package Project.OpenBook.Repository.topic;

import Project.OpenBook.Domain.Topic;
import Project.OpenBook.Dto.topic.TopicAdminDto;
import com.querydsl.core.Tuple;

import java.util.List;
import java.util.Optional;

public interface  TopicRepositoryCustom {

    public Topic queryRandTopicByCategory(String categoryName);

    public Topic queryTopicByDescription(Long descriptionId);

    public Topic queryTopicByChoice(Long choiceId);


    public List<Tuple> queryAdminChapterDto(Integer chapterNum);

    public TopicAdminDto queryTopicAdminDto(String topicTitle);

    public List<Tuple> queryTimeFlowQuestion(Integer num);

    public List<String> queryTopicTitleInChapter(Integer num);

    public List<String> queryWrongTopicTitle(String topicTitle, int size);

    public Optional<Topic> queryTopicByNumber(Integer chapterNum, Integer topicNum);
}
