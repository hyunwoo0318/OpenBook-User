package Project.OpenBook.Repository.choice;

import Project.OpenBook.Domain.Choice;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

public interface ChoiceRepositoryCustom {

    public List<Choice> queryChoiceByTopicTitle(String topicTitle);

    public Choice queryRandChoiceByChoice(Long choiceId);

    public List<Choice> queryChoicesById(List<Long> choiceIdList);

    public Choice  queryRandChoiceByTopic(String topicTitle);

    public List<Choice> queryRandChoicesByCategory(String exceptTopicTitle, String categoryName, int num);

    public List<Choice> queryRandChoicesByCategory(String categoryName, int num);

    @Transactional
    public List<Choice> queryChoicesType2(LocalDate startDate, LocalDate endDate, int num, int interval, String categoryName);

    @Transactional
    public List<Choice> queryChoicesType3(LocalDate startDate,LocalDate endDate, int num, int interval, String categoryName);

    @Transactional
    public List<Choice> queryChoicesType4(LocalDate startDate,LocalDate endDate, int num, int interval, String categoryName);


    public Choice queryRandChoiceByTime(LocalDate startDate, LocalDate endDate);
}
