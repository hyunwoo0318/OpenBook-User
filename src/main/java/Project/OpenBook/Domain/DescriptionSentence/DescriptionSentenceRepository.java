package Project.OpenBook.Domain.DescriptionSentence;

import Project.OpenBook.Domain.Description.Domain.Description;
import Project.OpenBook.Domain.Sentence.Domain.Sentence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DescriptionSentenceRepository extends JpaRepository<DescriptionSentence, Long>, DescriptionSentenceRepositoryCustom {

    public void deleteByDescriptionAndSentence(Description description, Sentence sentence);
}
