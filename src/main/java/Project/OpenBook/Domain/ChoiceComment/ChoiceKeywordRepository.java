package Project.OpenBook.Domain.ChoiceComment;

import Project.OpenBook.Domain.Choice.Domain.Choice;
import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.security.Key;

public interface ChoiceKeywordRepository extends JpaRepository<ChoiceKeyword, Long>, ChoiceKeywordRepositoryCustom {

    public void deleteByChoiceAndKeyword(Choice choice, Keyword keyword);
}
