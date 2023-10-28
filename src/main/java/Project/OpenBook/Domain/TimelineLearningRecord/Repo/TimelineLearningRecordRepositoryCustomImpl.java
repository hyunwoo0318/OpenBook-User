package Project.OpenBook.Domain.TimelineLearningRecord.Repo;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.TimelineLearningRecord.Domain.TimelineLearningRecord;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static Project.OpenBook.Domain.Era.QEra.era;
import static Project.OpenBook.Domain.Timeline.Domain.QTimeline.timeline;
import static Project.OpenBook.Domain.TimelineLearningRecord.Domain.QTimelineLearningRecord.timelineLearningRecord;

@Repository
@RequiredArgsConstructor
public class TimelineLearningRecordRepositoryCustomImpl implements TimelineLearningRecordRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<TimelineLearningRecord> queryTimelineLearningRecord(Customer customer) {
        return queryFactory.selectFrom(timelineLearningRecord)
                .leftJoin(timelineLearningRecord.timeline, timeline).fetchJoin()
                .leftJoin(timeline.era, era).fetchJoin()
                .where(timelineLearningRecord.customer.eq(customer))
                .fetch();
    }

    @Override
    public List<TimelineLearningRecord> queryTimelineLearningRecordInKeywords(Customer customer, List<Long> timelineIdList) {
        return queryFactory.selectFrom(timelineLearningRecord)
                .leftJoin(timelineLearningRecord.timeline, timeline).fetchJoin()
                .where(timelineLearningRecord.customer.eq(customer))
                .where(timelineLearningRecord.timeline.id.in(timelineIdList))
                .fetch();
    }

}
