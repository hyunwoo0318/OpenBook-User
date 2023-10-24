package Project.OpenBook.Domain.JJH.JJHList;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JJHListRepository extends JpaRepository<JJHList, Long>, JJHListRepositoryCustom {
}
