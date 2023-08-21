package Project.OpenBook.Repository.chapterProgress;

import Project.OpenBook.Domain.ChapterProgress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChapterProgressRepository extends JpaRepository<ChapterProgress, Long>, ChapterProgressRepositoryCustom {
}
