package Project.OpenBook.Repository.choice;

import Project.OpenBook.Domain.Category;
import Project.OpenBook.Domain.Choice;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

public interface ChoiceRepositoryCustom {

    public List<Choice> queryChoiceByTopicTitle(String topicTitle);



    public Choice queryRandChoiceByChoice(Long choiceId);

    public List<Choice> queryChoicesById(List<Long> choiceIdList);

    public Choice  queryRandChoiceByTopic(Long topicId, Long descriptionId);

    public List<Choice> queryRandChoicesByCategory(String exceptTopicTitle, String categoryName, int num);

    public List<Choice> queryRandChoicesByCategory(String categoryName, int num);

    @Transactional
    public List<Choice> queryChoicesType2(String topicTitle, Integer startDate, Integer endDate, int num, int interval, String categoryName);

    @Transactional
    public List<Choice> queryChoicesType3(String topicTitle, Integer startDate,Integer endDate, int num, int interval, String categoryName);

    @Transactional
    public List<Choice> queryChoicesType4(String topicTitle, Integer startDate,Integer endDate, int num, int interval, String categoryName);

    public Category queryCategoryByChoice(Long choiceId);

    public Choice queryRandChoiceByTime(Integer startDate, Integer endDate);
}
