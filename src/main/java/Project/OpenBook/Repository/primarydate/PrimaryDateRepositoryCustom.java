package Project.OpenBook.Repository.primarydate;

import Project.OpenBook.Domain.PrimaryDate;

import java.util.List;

public interface PrimaryDateRepositoryCustom {

    public List<PrimaryDate> queryDatesByTopic(String topicTitle);

}
