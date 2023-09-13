package Project.OpenBook.Domain.Category.Repository;

import Project.OpenBook.Domain.Category.Domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    public Optional<Category> findCategoryByName(String name);

}
