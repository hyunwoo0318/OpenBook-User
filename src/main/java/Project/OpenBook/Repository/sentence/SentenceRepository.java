package Project.OpenBook.Repository.sentence;


import Project.OpenBook.Domain.Sentence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SentenceRepository extends JpaRepository<Sentence,  Long>, SentenceRepositoryCustom {
}
