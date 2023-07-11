package Project.OpenBook.Repository.Sentence;


import Project.OpenBook.Domain.Sentence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SentenceRepository extends JpaRepository<Sentence,  Long>, SentenceRepositoryCustom {
}
