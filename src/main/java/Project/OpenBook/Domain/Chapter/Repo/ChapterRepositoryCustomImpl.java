package Project.OpenBook.Domain.Chapter.Repo;

import static Project.OpenBook.Domain.Chapter.Domain.QChapter.chapter;

import Project.OpenBook.Domain.Chapter.Domain.Chapter;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ChapterRepositoryCustomImpl implements ChapterRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Chapter> queryChaptersWithjjhList() {
        return queryFactory.selectFrom(chapter).leftJoin(chapter.jjhLists).fetchJoin().fetch();
    }

    @Override
    public List<Chapter> searchChapter(String input) {
        return queryFactory.selectFrom(chapter).where(chapter.title.contains(input)).fetch();
    }
}
