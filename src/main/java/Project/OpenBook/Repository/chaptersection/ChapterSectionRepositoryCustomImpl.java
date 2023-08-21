package Project.OpenBook.Repository.chaptersection;

import Project.OpenBook.Domain.ChapterSection;
import Project.OpenBook.Domain.QChapterSection;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static Project.OpenBook.Domain.QChapterSection.*;

@RequiredArgsConstructor
@Repository
public class ChapterSectionRepositoryCustomImpl implements ChapterSectionRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    @Override
    public List<ChapterSection> queryChapterSection(Long customerId, Integer num) {
        return queryFactory.selectFrom(chapterSection)
                .where(chapterSection.chapter.number.eq(num))
                .where(chapterSection.customer.id.eq(customerId))
                .fetch();
    }

    @Override
    public List<ChapterSection> queryChapterSection(Integer num) {
        return queryFactory.selectFrom(chapterSection)
                .where(chapterSection.chapter.number.eq(num))
                .fetch();
    }

    @Override
    public List<ChapterSection> queryChapterSection(Long customerId) {
        return queryFactory.selectFrom(chapterSection)
                .where(chapterSection.customer.id.eq(customerId))
                .fetch();
    }

    @Override
    public Optional<ChapterSection> queryChapterSection(Long customerId, Integer chapterNum, String content) {
        ChapterSection findChapterSection = queryFactory.selectFrom(chapterSection)
                .where(chapterSection.chapter.number.eq(chapterNum))
                .where(chapterSection.customer.id.eq(customerId))
                .where(chapterSection.content.eq(content))
                .fetchOne();
        return Optional.ofNullable(findChapterSection);

    }
}
