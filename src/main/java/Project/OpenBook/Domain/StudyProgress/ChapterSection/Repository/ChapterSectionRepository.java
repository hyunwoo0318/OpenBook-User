package Project.OpenBook.Domain.StudyProgress.ChapterSection.Repository;

import Project.OpenBook.Domain.StudyProgress.ChapterSection.Domain.ChapterSection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChapterSectionRepository extends ChapterSectionRepositoryCustom, JpaRepository<ChapterSection, Long> {
}
