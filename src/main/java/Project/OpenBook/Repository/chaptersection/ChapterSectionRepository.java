package Project.OpenBook.Repository.chaptersection;

import Project.OpenBook.Domain.ChapterSection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChapterSectionRepository extends ChapterSectionRepositoryCustom, JpaRepository<ChapterSection, Long> {
}
