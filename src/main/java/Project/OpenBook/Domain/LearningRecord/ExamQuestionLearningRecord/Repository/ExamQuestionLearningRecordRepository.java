package Project.OpenBook.Domain.LearningRecord.ExamQuestionLearningRecord.Repository;

import Project.OpenBook.Domain.LearningRecord.ExamQuestionLearningRecord.Domain.ExamQuestionLearningRecord;
import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.ExamQuestion.Domain.ExamQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ExamQuestionLearningRecordRepository extends JpaRepository<ExamQuestionLearningRecord, Long>, ExamQuestionLearningRecordRepositoryCustom {

    public Optional<ExamQuestionLearningRecord> findByCustomerAndExamQuestion(Customer customer, ExamQuestion examQuestion);

}