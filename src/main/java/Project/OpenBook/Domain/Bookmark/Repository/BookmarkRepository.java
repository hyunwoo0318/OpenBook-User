package Project.OpenBook.Domain.Bookmark.Repository;

import Project.OpenBook.Domain.Bookmark.Domain.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long>, BookmarkRepositoryCustom {
}
