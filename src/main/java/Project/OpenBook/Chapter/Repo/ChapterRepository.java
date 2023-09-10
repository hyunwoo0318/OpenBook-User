package Project.OpenBook.Chapter.Repo;

import Project.OpenBook.Chapter.Domain.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long>, ChapterRepositoryCustom {

    public Optional<Chapter> findOneByNumber(int number);
}
