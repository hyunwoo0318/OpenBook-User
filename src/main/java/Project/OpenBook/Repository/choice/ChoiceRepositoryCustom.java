package Project.OpenBook.Repository.choice;

import Project.OpenBook.Domain.Choice;

import java.util.List;

public interface ChoiceRepositoryCustom {

    public List<Choice> queryChoiceByTopicTitle(String topicTitle);

    public List<Choice> queryChoicesById(List<Long> choiceIdList);
}
