package Project.OpenBook.Repository.chapter;

import Project.OpenBook.Domain.Chapter;
import Project.OpenBook.Domain.QChapterSection;
import com.querydsl.core.Tuple;
import com.querydsl.core.group.Group;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import static Project.OpenBook.Domain.QChapter.chapter;
import static Project.OpenBook.Domain.QChapterProgress.chapterProgress;
import static Project.OpenBook.Domain.QChapterSection.chapterSection;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;

@RequiredArgsConstructor
public class ChapterRepositoryCustomImpl implements ChapterRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Map<Chapter, Group> queryChapterUserDtos(Long customerId) {
        Map<Chapter, Group> map = queryFactory.from(chapter)
                .leftJoin(chapterProgress).on(chapterProgress.chapter.eq(chapter))
                .leftJoin(chapterSection).on(chapterSection.chapter.eq(chapter))
                .transform(groupBy(chapter).as(chapterProgress, list(chapterSection)));
        return map;
    }
}
