package Project.OpenBook.Round.Repo;

import Project.OpenBook.Round.Domain.Round;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoundRepository extends JpaRepository<Round, Long> {

    public Optional<Round> findRoundByNumber(Integer number);

}
