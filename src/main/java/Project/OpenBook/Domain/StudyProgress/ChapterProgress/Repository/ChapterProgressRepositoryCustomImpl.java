package Project.OpenBook.Domain.StudyProgress.ChapterProgress.Repository;

import Project.OpenBook.Domain.Chapter.Domain.QChapter;
import Project.OpenBook.Domain.StudyProgress.ChapterProgress.Domain.ChapterProgress;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static Project.OpenBook.Domain.Chapter.Domain.QChapter.chapter;
import static Project.OpenBook.Domain.StudyProgress.ChapterProgress.Domain.QChapterProgress.*;

@RequiredArgsConstructor
public class ChapterProgressRepositoryCustomImpl implements ChapterProgressRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<ChapterProgress> queryChapterProgress(Long customerId, Integer chapterNum) {
        ChapterProgress findChapterProgress = queryFactory.selectFrom(chapterProgress)
                .where(chapterProgress.customer.id.eq(customerId))
                .where(chapterProgress.chapter.number.eq(chapterNum))
                .fetchOne();
        return Optional.ofNullable(findChapterProgress);
    }

    @Override
    public List<ChapterProgress> queryChapterProgressesWithChapter(Long customerId) {
        return queryFactory.selectFrom(chapterProgress)
                .leftJoin(chapterProgress.chapter, chapter).fetchJoin()
                .where(chapterProgress.customer.id.eq(customerId))
                .fetch();
    }

}
