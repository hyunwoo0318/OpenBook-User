package Project.OpenBook.Repository.chapter;

import Project.OpenBook.Domain.*;
import com.querydsl.core.Tuple;
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
    public List<Tuple> queryChapterUserDtos(Long customerId) {
        return queryFactory.select(chapter.title, chapter.number, chapterProgress.progress)
                    .from(chapter)
                    .leftJoin(chapterProgress).on(chapterProgress.chapter.eq(chapter))
                    .fetch();
    }
}
