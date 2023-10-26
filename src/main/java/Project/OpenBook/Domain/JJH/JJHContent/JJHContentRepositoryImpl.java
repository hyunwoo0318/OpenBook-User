package Project.OpenBook.Domain.JJH.JJHContent;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static Project.OpenBook.Domain.Chapter.Domain.QChapter.chapter;
import static Project.OpenBook.Domain.Era.QEra.era;
import static Project.OpenBook.Domain.JJH.JJHContent.QJJHContent.jJHContent;
import static Project.OpenBook.Domain.Timeline.Domain.QTimeline.timeline;
import static Project.OpenBook.Domain.Topic.Domain.QTopic.topic;

@Repository
@RequiredArgsConstructor
public class JJHContentRepositoryImpl implements JJHContentRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<JJHContent> queryJJHContents() {
        return queryFactory.selectFrom(jJHContent)
                .leftJoin(jJHContent.chapter, chapter).fetchJoin()
                .leftJoin(jJHContent.topic, topic).fetchJoin()
                .leftJoin(jJHContent.timeline, timeline).fetchJoin()
                .leftJoin(timeline.era, era).fetchJoin()
                .fetch();
    }
}
