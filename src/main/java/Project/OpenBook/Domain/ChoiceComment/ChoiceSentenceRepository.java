package Project.OpenBook.Domain.ChoiceComment;

import Project.OpenBook.Domain.Choice.Domain.Choice;
import Project.OpenBook.Domain.Sentence.Domain.Sentence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChoiceSentenceRepository extends JpaRepository<ChoiceSentence, Long>, ChoiceSentenceRepositoryCustom {

    public void deleteByChoiceAndSentence(Choice choice, Sentence sentence);

}
