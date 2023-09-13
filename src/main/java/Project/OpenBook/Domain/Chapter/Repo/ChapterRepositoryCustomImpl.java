package Project.OpenBook.Domain.Chapter.Repo;

import Project.OpenBook.Domain.Chapter.Domain.Chapter;
import Project.OpenBook.Domain.StudyProgress.ChapterProgress.Domain.ChapterProgress;
import com.querydsl.core.group.GroupBy;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import static Project.OpenBook.Chapter.Domain.QChapter.chapter;
import static Project.OpenBook.Domain.QChapterProgress.chapterProgress;

@RequiredArgsConstructor
public class ChapterRepositoryCustomImpl implements ChapterRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Map<Chapter, ChapterProgress> queryChapterWithProgress(Long customerId) {
        Map<Chapter, ChapterProgress> map = queryFactory.from(chapter)
                .leftJoin(chapterProgress).on(chapterProgress.chapter.eq(chapter))
                .transform(GroupBy.groupBy(chapter).as(chapterProgress));
        return map;
    }

    @Override
    public Integer queryMaxChapterNum() {
        return queryFactory.select(chapter.number.max())
                .from(chapter)
                .fetchOne();
    }


}
