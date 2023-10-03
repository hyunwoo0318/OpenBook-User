package Project.OpenBook.Domain.ChoiceComment.ChoiceKeyword;

import Project.OpenBook.Domain.Choice.Domain.Choice;
import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChoiceKeywordRepository extends JpaRepository<ChoiceKeyword, Long>, ChoiceKeywordRepositoryCustom {

    public void deleteByChoiceAndKeyword(Choice choice, Keyword keyword);
}
