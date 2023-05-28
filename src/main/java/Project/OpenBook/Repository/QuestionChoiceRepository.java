package Project.OpenBook.Repository;

import Project.OpenBook.Domain.QuestionChoice;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionChoiceRepository extends JpaRepository<QuestionChoice, Long> {
}
