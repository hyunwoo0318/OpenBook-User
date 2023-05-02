package Project.OpenBook.Repository.choice;

import Project.OpenBook.Domain.Choice;
import Project.OpenBook.Domain.Topic;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ChoiceRepositoryCustom {

    public List<Choice> queryChoiceByTopicTitle(String topicTitle);

    public List<Choice> queryChoicesById(List<Long> choiceIdList);

    public Choice  queryRandChoiceByTopic(String topicTitle);

    public List<Choice> queryRandChoicesByCategory(String exceptTopicTitle, String categoryName, int num);

    public List<Choice> queryRandChoicesByTime(LocalDate startDate, LocalDate endDate, int num, int interval, String categoryName);

    public Choice queryRandChoiceByTime(LocalDate startDate, LocalDate endDate);
}
