package Project.OpenBook.Repository.chapter;

import com.querydsl.core.group.Group;


import java.util.Map;

public interface ChapterRepositoryCustom {

    public Map<Integer, Group> queryChapterUserDtos(Long customerId);
}
