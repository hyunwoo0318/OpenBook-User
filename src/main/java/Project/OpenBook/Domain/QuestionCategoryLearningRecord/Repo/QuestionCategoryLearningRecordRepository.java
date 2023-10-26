package Project.OpenBook.Domain.QuestionCategoryLearningRecord.Repo;

import Project.OpenBook.Domain.QuestionCategory.Domain.QuestionCategory;
import Project.OpenBook.Domain.QuestionCategoryLearningRecord.Domain.QuestionCategoryLearningRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionCategoryLearningRecordRepository
        extends JpaRepository<QuestionCategoryLearningRecord,Long>, QuestionCategoryLearningRecordRepositoryCustom {



}
