package Project.OpenBook.Domain.Timeline.Repo;

import static Project.OpenBook.Domain.Era.QEra.era;
import static Project.OpenBook.Domain.Timeline.Domain.QTimeline.timeline;

import Project.OpenBook.Domain.Timeline.Domain.Timeline;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TimelineRepositoryCustomImpl implements TimelineRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Timeline> queryTimelinesWithEraAndjjhList() {
        return queryFactory.selectFrom(timeline)
            .leftJoin(timeline.era, era).fetchJoin()
            .leftJoin(timeline.jjhLists).fetchJoin()
            .fetch();
    }

    @Override
    public Optional<Timeline> queryTimelineWithEra(Long id) {
        Timeline findTimeline = queryFactory.selectFrom(timeline)
            .leftJoin(timeline.era, era).fetchJoin()
            .where(timeline.id.eq(id))
            .fetchOne();
        return Optional.ofNullable(findTimeline);
    }

    @Override
    public Optional<Long> queryRandomTimeline() {
        Long findTimelineId = queryFactory.select(timeline.id)
            .from(timeline)
            .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
            .limit(1)
            .fetchOne();
        return Optional.ofNullable(findTimelineId);
    }

    @Override
    public List<Timeline> queryAllForInit() {
        return queryFactory.selectFrom(timeline)
            .leftJoin(timeline.era, era).fetchJoin()
            .fetch();
    }
}
