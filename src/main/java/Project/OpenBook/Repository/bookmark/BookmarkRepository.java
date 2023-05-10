package Project.OpenBook.Repository.bookmark;

import Project.OpenBook.Domain.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long>, BookmarkRepositoryCustom {
}
