package Project.OpenBook.Domain.DescriptionComment.DescriptionKeyword;

import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DescriptionKeywordRepository extends JpaRepository<DescriptionKeyword, Long>,
    DescriptionKeywordRepositoryCustom {


    public void deleteByKeyword(Keyword keyword);
}
