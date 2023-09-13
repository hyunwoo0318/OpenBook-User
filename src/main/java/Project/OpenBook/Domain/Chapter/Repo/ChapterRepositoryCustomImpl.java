package Project.OpenBook.Domain.Chapter.Repo;

import Project.OpenBook.Domain.Chapter.Domain.Chapter;
import Project.OpenBook.Domain.StudyProgress.ChapterProgress.Domain.ChapterProgress;
import com.querydsl.core.group.GroupBy;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Optional;

import static Project.OpenBook.Domain.Chapter.Domain.QChapter.chapter;
import static Project.OpenBook.Domain.StudyProgress.ChapterProgress.Domain.QChapterProgress.chapterProgress;


@RequiredArgsConstructor
public class ChapterRepositoryCustomImpl implements ChapterRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Integer> queryMaxChapterNum() {
        Integer findNum = queryFactory.select(chapter.number.max())
                .from(chapter)
                .fetchOne();
        return Optional.ofNullable(findNum);
    }


}
