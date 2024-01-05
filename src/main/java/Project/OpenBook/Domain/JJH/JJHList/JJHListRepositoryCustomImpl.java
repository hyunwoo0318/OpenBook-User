package Project.OpenBook.Domain.JJH.JJHList;

import static Project.OpenBook.Domain.Chapter.Domain.QChapter.chapter;
import static Project.OpenBook.Domain.JJH.JJHList.QJJHList.jJHList;
import static Project.OpenBook.Domain.Timeline.Domain.QTimeline.timeline;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JJHListRepositoryCustomImpl implements JJHListRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<JJHList> queryJJHListsWithChapterAndTimeline() {
        return queryFactory.selectFrom(jJHList).distinct()
            .leftJoin(jJHList.chapter, chapter).fetchJoin()
            .leftJoin(chapter.topicList).fetchJoin()
            .leftJoin(jJHList.timeline, timeline).fetchJoin()
            .fetch();
    }


    @Override
    public Optional<JJHList> queryJJHList(Integer jjhNumber) {
        JJHList findJJHList = queryFactory.selectFrom(jJHList).distinct()
            .leftJoin(jJHList.chapter, chapter).fetchJoin()
            .leftJoin(chapter.topicList).fetchJoin()
            .leftJoin(jJHList.timeline, timeline).fetchJoin()
            .where(jJHList.number.eq(jjhNumber))
            .fetchOne();
        return Optional.ofNullable(findJJHList);
    }
}
