package Project.OpenBook.Domain.AnswerNote.Service;

import static Project.OpenBook.Constants.ErrorCode.QUESTION_NOT_FOUND;

import Project.OpenBook.Domain.AnswerNote.Service.Dto.AnswerNoteDto;
import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.ExamQuestion.Domain.ExamQuestion;
import Project.OpenBook.Domain.ExamQuestion.Repo.ExamQuestionRepository;
import Project.OpenBook.Domain.LearningRecord.ExamQuestionLearningRecord.Domain.ExamQuestionLearningRecord;
import Project.OpenBook.Domain.LearningRecord.ExamQuestionLearningRecord.Repository.ExamQuestionLearningRecordRepository;
import Project.OpenBook.Handler.Exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AnswerNoteService {

    private final ExamQuestionLearningRecordRepository examQuestionLearningRecordRepository;
    private final ExamQuestionRepository examQuestionRepository;

    @Transactional
    public void addAnswerNote(Customer customer, AnswerNoteDto answerNoteDto) {
        Long questionId = answerNoteDto.getQuestionId();

        ExamQuestion examQuestion = examQuestionRepository.findById(questionId).orElseThrow(() -> {
            throw new CustomException(QUESTION_NOT_FOUND);
        });

        ExamQuestionLearningRecord record = examQuestionLearningRecordRepository.findByCustomerAndExamQuestion(
            customer, examQuestion).orElseGet(() -> {
            ExamQuestionLearningRecord newRecord = new ExamQuestionLearningRecord(customer,
                examQuestion);
            examQuestionLearningRecordRepository.save(newRecord);
            return newRecord;
        });

        record.updateAnswerNoted(true);
    }

    @Transactional
    public void deleteAnswerNote(Customer customer, AnswerNoteDto answerNoteDto) {
        Long questionId = answerNoteDto.getQuestionId();

        ExamQuestion examQuestion = examQuestionRepository.findById(questionId).orElseThrow(() -> {
            throw new CustomException(QUESTION_NOT_FOUND);
        });

        ExamQuestionLearningRecord record = examQuestionLearningRecordRepository.findByCustomerAndExamQuestion(
            customer, examQuestion).orElseGet(() -> {
            ExamQuestionLearningRecord newRecord = new ExamQuestionLearningRecord(customer,
                examQuestion);
            examQuestionLearningRecordRepository.save(newRecord);
            return newRecord;
        });

        record.updateAnswerNoted(false);
    }

}
