package Project.OpenBook.Repository.chapter;

import Project.OpenBook.Domain.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {

    public List<Chapter> findAll();

    public Optional<Chapter> findOneByNumber(int number);
}
