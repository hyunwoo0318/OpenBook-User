package Project.OpenBook.Domain.JJH.JJHListProgress;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static Project.OpenBook.Domain.Chapter.Domain.QChapter.chapter;
import static Project.OpenBook.Domain.Customer.Domain.QCustomer.customer;
import static Project.OpenBook.Domain.Era.QEra.era;
import static Project.OpenBook.Domain.JJH.JJHList.QJJHList.jJHList;
import static Project.OpenBook.Domain.JJH.JJHListProgress.QJJHListProgress.jJHListProgress;
import static Project.OpenBook.Domain.Timeline.Domain.QTimeline.timeline;

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

    @Override
    public List<JJHListProgress> queryAllJJHList() {
        return queryFactory.selectFrom(jJHListProgress)
                .leftJoin(jJHListProgress.jjhList, jJHList).fetchJoin()
                .leftJoin(jJHListProgress.customer, customer).fetchJoin()
                .fetch();
    }
}
