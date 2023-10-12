package Project.OpenBook.Domain.Keyword.KeywordPrimaryDate.Repository;

import Project.OpenBook.Domain.Keyword.KeywordPrimaryDate.Domain.KeywordPrimaryDate;

import java.util.List;

public interface KeywordPrimaryDateRepositoryCustom {

    public List<KeywordPrimaryDate> queryKeywordPrimaryDateInChapter(Integer chapterNum);
}
