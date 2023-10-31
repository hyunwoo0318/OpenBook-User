package Project.OpenBook.Domain.DescriptionComment.DescriptionKeyword;

import Project.OpenBook.Domain.Description.Domain.Description;
import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DescriptionKeywordRepository extends JpaRepository<DescriptionKeyword, Long>, DescriptionKeywordRepositoryCustom {

    public void deleteByDescriptionAndKeyword(Description description, Keyword keyword);

    public void deleteByKeyword(Keyword keyword);
}
