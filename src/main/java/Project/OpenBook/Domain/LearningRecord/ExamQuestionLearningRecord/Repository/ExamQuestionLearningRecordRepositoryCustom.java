package Project.OpenBook.Domain.LearningRecord.ExamQuestionLearningRecord.Repository;


import Project.OpenBook.Domain.LearningRecord.ExamQuestionLearningRecord.Domain.ExamQuestionLearningRecord;
import Project.OpenBook.Domain.Customer.Domain.Customer;

import java.util.List;

public interface ExamQuestionLearningRecordRepositoryCustom {

//    public Optional<AnswerNote> queryAnswerNote(Long customerId, Long questionId);
//
    public List<ExamQuestionLearningRecord> queryExamQuestionLearningRecords(Integer roundNumber, Customer customer);
    public List<ExamQuestionLearningRecord> queryExamQuestionLearningRecordsAnswerNoted (Customer customer);
    public List<ExamQuestionLearningRecord> queryExamQuestionLearningRecords(Customer customer);

    public List<ExamQuestionLearningRecord> queryExamQuestionLearningRecords(Customer customer, List<Long> examQuestionIdList);
}

