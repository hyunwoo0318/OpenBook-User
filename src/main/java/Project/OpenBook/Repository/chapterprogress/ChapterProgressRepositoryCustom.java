package Project.OpenBook.Repository.chapterprogress;

import Project.OpenBook.Domain.ChapterProgress;

import java.util.List;
import java.util.Optional;

public interface ChapterProgressRepositoryCustom {

    public Optional<ChapterProgress> queryChapterProgress(Long customerId, Integer num);

    public List<ChapterProgress> queryChapterProgress(Integer num);

    public List<ChapterProgress> queryChapterProgress(Long customerId);
}
