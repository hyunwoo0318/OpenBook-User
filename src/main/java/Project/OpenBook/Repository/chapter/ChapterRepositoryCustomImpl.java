package Project.OpenBook.Repository.chapter;

import Project.OpenBook.Domain.*;
import com.querydsl.core.group.Group;
import com.querydsl.core.group.GroupBy;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import static Project.OpenBook.Domain.QChapter.chapter;
import static Project.OpenBook.Domain.QChapterProgress.chapterProgress;
import static Project.OpenBook.Domain.QTopic.topic;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;

@RequiredArgsConstructor
public class ChapterRepositoryCustomImpl implements ChapterRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Map<Integer, Group> queryChapterUserDtos(Long customerId) {
            return queryFactory.from(chapter)
                .leftJoin(topic).on(topic.chapter.eq(chapter))
                .leftJoin(chapterProgress).on(chapterProgress.chapter.eq(chapter))
                    .where(chapterProgress.customer.id.eq(customerId))
                .transform(groupBy(chapter.number).as(chapter.number, chapter.title, chapterProgress.progress, list(topic)));
    }
}