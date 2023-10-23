package Project.OpenBook.Domain.TimeLine;

import Project.OpenBook.Domain.Era.QEra;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static Project.OpenBook.Domain.Era.QEra.era;
import static Project.OpenBook.Domain.TimeLine.QTimeline.timeline;

@Repository
@RequiredArgsConstructor
public class TimelineRepositoryCustomImpl implements TimelineRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    @Override
    public List<Timeline> queryTimelinesWithEra() {
        return queryFactory.selectFrom(timeline)
                .leftJoin(timeline.era, era).fetchJoin()
                .fetch();
    }
}
