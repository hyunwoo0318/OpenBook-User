package Project.OpenBook.Repository.chapterprogress;

import Project.OpenBook.Domain.ChapterProgress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChapterProgressRepository extends ChapterProgressRepositoryCustom, JpaRepository<ChapterProgress, Long> {
}