package Project.OpenBook.Repository.chapter;

import Project.OpenBook.Domain.Chapter;
import Project.OpenBook.Domain.ChapterProgress;
import Project.OpenBook.Domain.QChapterSection;
import Project.OpenBook.Domain.QTopic;
import com.querydsl.core.Tuple;
import com.querydsl.core.group.Group;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static Project.OpenBook.Domain.QChapter.chapter;
import static Project.OpenBook.Domain.QChapterProgress.chapterProgress;
import static Project.OpenBook.Domain.QChapterSection.chapterSection;
import static Project.OpenBook.Domain.QTopic.topic;
import static com.querydsl.core.group.GroupBy.*;

@RequiredArgsConstructor
public class ChapterRepositoryCustomImpl implements ChapterRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Map<Chapter, ChapterProgress> queryChapterWithProgress(Long customerId) {
        Map<Chapter, ChapterProgress> map = queryFactory.from(chapter)
                .leftJoin(chapterProgress).on(chapterProgress.chapter.eq(chapter))
                .transform(groupBy(chapter).as(chapterProgress));
        return map;
    }

    @Override
    public Integer queryMaxChapterNum() {
        return queryFactory.select(chapter.number.max())
                .from(chapter)
                .fetchOne();
    }

    @Override
    public Optional<Chapter> queryChapterWithTopic(int num) {
        Chapter findChapter = queryFactory.select(chapter).distinct()
                .from(chapter)
                .leftJoin(topic).on(topic.chapter.eq(chapter)).fetchJoin()
                .where(chapter.number.eq(num))
                .fetchOne();
        return Optional.ofNullable(findChapter);
    }

    @Override
    public List<Chapter> queryChapterListWithTopic() {
        return queryFactory.select(chapter).distinct()
                .from(chapter)
                .leftJoin(topic).on(topic.chapter.eq(chapter)).fetchJoin()
                .fetch();
    }
}
