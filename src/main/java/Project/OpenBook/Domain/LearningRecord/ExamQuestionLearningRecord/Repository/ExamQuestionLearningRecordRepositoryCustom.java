package Project.OpenBook.Domain.LearningRecord.ExamQuestionLearningRecord.Repository;


import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.LearningRecord.ExamQuestionLearningRecord.Domain.ExamQuestionLearningRecord;
import java.util.List;

public interface ExamQuestionLearningRecordRepositoryCustom {

    public List<ExamQuestionLearningRecord> queryExamQuestionLearningRecords(Integer roundNumber,
        Customer customer);

    public List<ExamQuestionLearningRecord> queryExamQuestionLearningRecordsAnswerNoted(
        Customer customer);

    public List<ExamQuestionLearningRecord> queryExamQuestionLearningRecords(Customer customer,
        List<Long> examQuestionIdList);
}

