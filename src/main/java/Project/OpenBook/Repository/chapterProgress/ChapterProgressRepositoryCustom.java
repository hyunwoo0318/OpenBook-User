package Project.OpenBook.Repository.chapterProgress;

import Project.OpenBook.Domain.ChapterProgress;

import java.util.List;
import java.util.Optional;

public interface ChapterProgressRepositoryCustom {

    public Optional<ChapterProgress> queryChapterProgress(Long customerId, Integer chapterNum);

    public List<ChapterProgress> queryChapterProgress(Integer chapterNum);
}
