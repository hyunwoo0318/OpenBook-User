package Project.OpenBook.Domain.Chapter.Repo;

import Project.OpenBook.Domain.Chapter.Domain.Chapter;
import Project.OpenBook.Domain.StudyProgress.ChapterProgress.Domain.ChapterProgress;


import java.util.Map;
import java.util.Optional;

public interface ChapterRepositoryCustom {

    public Optional<Integer> queryMaxChapterNum();
}
