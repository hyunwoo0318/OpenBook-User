package Project.OpenBook.Domain.Keyword.KeywordPrimaryDate.Repository;

import Project.OpenBook.Domain.Keyword.KeywordPrimaryDate.Domain.KeywordPrimaryDate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static Project.OpenBook.Domain.Keyword.Domain.QKeyword.keyword;
import static Project.OpenBook.Domain.Keyword.KeywordPrimaryDate.Domain.QKeywordPrimaryDate.keywordPrimaryDate;

@Repository
@RequiredArgsConstructor
public class KeywordPrimaryDateRepositoryCustomImpl implements KeywordPrimaryDateRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<KeywordPrimaryDate> queryKeywordPrimaryDateInChapter(Integer chapterNum) {
        if (chapterNum == -1) {
            return queryFactory.selectFrom(keywordPrimaryDate).distinct()
                    .leftJoin(keywordPrimaryDate.keyword, keyword).fetchJoin()
                    .fetch();
        }
        return queryFactory.selectFrom(keywordPrimaryDate).distinct()
                .leftJoin(keywordPrimaryDate.keyword, keyword).fetchJoin()
                .where(keyword.topic.chapter.number.eq(chapterNum))
                .fetch();
    }
}
