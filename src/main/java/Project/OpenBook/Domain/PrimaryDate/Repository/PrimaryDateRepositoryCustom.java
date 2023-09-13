package Project.OpenBook.Domain.PrimaryDate.Repository;

import Project.OpenBook.Domain.PrimaryDate.Domain.PrimaryDate;

import java.util.List;

public interface PrimaryDateRepositoryCustom {

    public List<PrimaryDate> queryDatesByTopic(Long topicId);

}
