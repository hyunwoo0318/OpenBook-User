package Project.OpenBook.Repository.chapterprogress;

import Project.OpenBook.Domain.ChapterProgress;
import Project.OpenBook.Domain.QChapterProgress;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static Project.OpenBook.Domain.QChapterProgress.chapterProgress;

@RequiredArgsConstructor
@Repository
public class ChapterProgressRepositoryCustomImpl implements ChapterProgressRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    @Override
    public ChapterProgress queryChapterProgress(Long customerId, Integer num) {
        return queryFactory.selectFrom(chapterProgress)
                .where(chapterProgress.chapter.number.eq(num))
                .where(chapterProgress.customer.id.eq(customerId))
                .fetchOne();
    }
}
