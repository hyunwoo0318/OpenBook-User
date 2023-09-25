package Project.OpenBook.Domain.Era;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EraRepository extends JpaRepository<Era, Long> {

    Optional<Era> findByName(String name);
}
