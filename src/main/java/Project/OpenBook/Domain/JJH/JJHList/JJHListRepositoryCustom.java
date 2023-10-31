package Project.OpenBook.Domain.JJH.JJHList;

import java.util.List;
import java.util.Optional;

public interface JJHListRepositoryCustom {

    public List<JJHList> queryJJHListsWithChapterAndTimeline();

    public Optional<JJHList> queryJJHList(Integer jjhNumber);
}
