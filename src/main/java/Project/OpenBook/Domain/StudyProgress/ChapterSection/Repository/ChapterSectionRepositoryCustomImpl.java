package Project.OpenBook.Domain.StudyProgress.ChapterSection.Repository;

import Project.OpenBook.Domain.StudyProgress.ChapterSection.Domain.ChapterSection;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static Project.OpenBook.Domain.StudyProgress.ChapterSection.Domain.QChapterSection.chapterSection;

@RequiredArgsConstructor
@Repository
public class ChapterSectionRepositoryCustomImpl implements ChapterSectionRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    @Override
    public List<ChapterSection> queryChapterSections(Long customerId, Integer num) {
        return queryFactory.selectFrom(chapterSection)
                .where(chapterSection.chapter.number.eq(num))
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
