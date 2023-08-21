package Project.OpenBook.Repository.chapter;

import Project.OpenBook.Domain.Chapter;
import com.querydsl.core.Tuple;
import com.querydsl.core.group.Group;


import java.util.List;
import java.util.Map;

public interface ChapterRepositoryCustom {

    public Map<Chapter, Group> queryChapterUserDtos(Long customerId);
}
