package Project.OpenBook.Domain.LearningRecord.QuestionCategoryLearningRecord.Repo;

import Project.OpenBook.Domain.LearningRecord.QuestionCategoryLearningRecord.Domain.QuestionCategoryLearningRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionCategoryLearningRecordRepository
        extends JpaRepository<QuestionCategoryLearningRecord,Long>, QuestionCategoryLearningRecordRepositoryCustom {



}
