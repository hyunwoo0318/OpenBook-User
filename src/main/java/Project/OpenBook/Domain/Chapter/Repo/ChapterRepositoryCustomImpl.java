package Project.OpenBook.Domain.Chapter.Repo;

import Project.OpenBook.Domain.Chapter.Domain.Chapter;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static Project.OpenBook.Domain.Chapter.Domain.QChapter.chapter;


@RequiredArgsConstructor
public class ChapterRepositoryCustomImpl implements ChapterRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Chapter> queryChaptersWithjjhList() {
        return queryFactory.selectFrom(chapter)
                .leftJoin(chapter.jjhLists).fetchJoin()
                .fetch();
    }


}
