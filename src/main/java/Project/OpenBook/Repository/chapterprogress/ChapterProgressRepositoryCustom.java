package Project.OpenBook.Repository.chapterprogress;

import Project.OpenBook.Domain.ChapterProgress;

import java.util.Optional;

public interface ChapterProgressRepositoryCustom {

    public Optional<ChapterProgress> queryChapterProgress(Long customerId, Integer num);
}
