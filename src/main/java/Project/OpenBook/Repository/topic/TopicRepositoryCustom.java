package Project.OpenBook.Repository.topic;

import Project.OpenBook.Domain.Keyword;
import Project.OpenBook.Domain.Topic;
import Project.OpenBook.Dto.topic.AdminChapterDto;
import Project.OpenBook.Dto.topic.TopicDto;
import com.querydsl.core.Tuple;

import java.util.List;

public interface  TopicRepositoryCustom {

    public Topic queryRandTopicByCategory(String categoryName);

    public Topic queryTopicByDescription(Long descriptionId);

    public Topic queryTopicByChoice(Long choiceId);


    public List<Tuple> queryAdminChapterDto(Integer chapterNum);

    public TopicDto queryTopicDto(String topicTitle);

    public List<Tuple> queryTimeFlowQuestion(Integer num);

    public List<String> queryTopicTitleInChapter(Integer num);

    public List<String> queryWrongTopicTitle(String topicTitle, int size);
}
