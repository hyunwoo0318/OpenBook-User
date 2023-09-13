package Project.OpenBook.Domain.StudyProgress.ChapterProgress.Repository;

import Project.OpenBook.Domain.StudyProgress.ChapterProgress.Domain.ChapterProgress;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

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

//    @Override
//    public List<ChapterProgress> queryChapterProgress(Integer chapterNum) {
//        return queryFactory.selectFrom(chapterProgress)
//                .where(chapterProgress.chapter.number.eq(chapterNum))
//                .fetch();
//    }
}
