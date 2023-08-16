package Project.OpenBook.Repository.chapter;

import com.querydsl.core.Tuple;
import com.querydsl.core.group.Group;


import java.util.List;
import java.util.Map;

public interface ChapterRepositoryCustom {

    public List<Tuple> queryChapterUserDtos(Long customerId);
}
