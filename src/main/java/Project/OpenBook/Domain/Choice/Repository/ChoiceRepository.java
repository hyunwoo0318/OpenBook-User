package Project.OpenBook.Domain.Choice.Repository;

import Project.OpenBook.Domain.Choice.Domain.Choice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChoiceRepository extends JpaRepository<Choice, Long>, ChoiceRepositoryCustom {

}
