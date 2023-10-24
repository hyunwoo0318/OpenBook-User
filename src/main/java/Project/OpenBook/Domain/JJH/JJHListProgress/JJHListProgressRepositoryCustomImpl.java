package Project.OpenBook.Domain.JJH.JJHListProgress;

import Project.OpenBook.Domain.Chapter.Domain.QChapter;
import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.Era.QEra;
import Project.OpenBook.Domain.JJH.JJHList.QJJHList;
import Project.OpenBook.Domain.QJJHListEntity;
import Project.OpenBook.Domain.TimeLine.QTimeline;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static Project.OpenBook.Domain.Chapter.Domain.QChapter.chapter;
import static Project.OpenBook.Domain.Era.QEra.era;
import static Project.OpenBook.Domain.JJH.JJHList.QJJHList.jJHList;
import static Project.OpenBook.Domain.JJH.JJHListProgress.QJJHListProgress.jJHListProgress;
import static Project.OpenBook.Domain.TimeLine.QTimeline.timeline;

@Repository
@RequiredArgsConstructor
public class JJHListProgressRepositoryCustomImpl implements JJHListProgressRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<JJHListProgress> queryJJHListProgressWithJJHList(Customer customer) {
        return queryFactory.selectFrom(jJHListProgress)
                .leftJoin(jJHListProgress.jjhList, jJHList).fetchJoin()
                .leftJoin(jJHList.chapter, chapter).fetchJoin()
                .leftJoin(jJHList.timeline, timeline).fetchJoin()
                .leftJoin(timeline.era, era).fetchJoin()
                .where(jJHListProgress.customer.eq(customer))
                .fetch();
    }
}
