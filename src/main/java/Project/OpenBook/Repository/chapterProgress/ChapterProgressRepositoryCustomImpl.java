package Project.OpenBook.Repository.chapterProgress;

import Project.OpenBook.Domain.ChapterProgress;
import Project.OpenBook.Domain.QChapterProgress;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static Project.OpenBook.Domain.QChapterProgress.chapterProgress;

@RequiredArgsConstructor
public class ChapterProgressRepositoryCustomImpl implements ChapterProgressRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<ChapterProgress> queryChapterProgress(Long customerId, Integer chapterNum) {
        ChapterProgress chapterProgress = queryFactory.selectFrom(QChapterProgress.chapterProgress)
                .where(QChapterProgress.chapterProgress.customer.id.eq(customerId))
                .where(QChapterProgress.chapterProgress.chapter.number.eq(chapterNum))
                .fetchOne();
        return Optional.ofNullable(chapterProgress);
    }

    @Override
    public List<ChapterProgress> queryChapterProgress(Integer chapterNum) {
        return queryFactory.selectFrom(chapterProgress)
                .where(chapterProgress.chapter.number.eq(chapterNum))
                .fetch();
    }
}