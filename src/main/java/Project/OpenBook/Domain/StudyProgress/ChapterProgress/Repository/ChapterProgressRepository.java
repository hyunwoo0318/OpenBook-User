package Project.OpenBook.Domain.StudyProgress.ChapterProgress.Repository;

import Project.OpenBook.Domain.StudyProgress.ChapterProgress.Domain.ChapterProgress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface
ChapterProgressRepository extends JpaRepository<ChapterProgress, Long>, ChapterProgressRepositoryCustom {
}
