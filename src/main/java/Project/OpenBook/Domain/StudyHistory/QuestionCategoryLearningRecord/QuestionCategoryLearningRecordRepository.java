package Project.OpenBook.Domain.StudyHistory.QuestionCategoryLearningRecord;

import Project.OpenBook.Domain.QuestionCategory.Domain.QuestionCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionCategoryLearningRecordRepository
        extends JpaRepository<QuestionCategory,Long>, QuestionCategoryLearningRecordRepositoryCustom {



}
