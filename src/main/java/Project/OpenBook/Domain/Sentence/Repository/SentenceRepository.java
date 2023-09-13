package Project.OpenBook.Domain.Sentence.Repository;


import Project.OpenBook.Domain.Sentence.Domain.Sentence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SentenceRepository extends JpaRepository<Sentence,  Long>, SentenceRepositoryCustom {
}
