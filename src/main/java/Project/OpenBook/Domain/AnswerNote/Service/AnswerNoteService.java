package Project.OpenBook.Domain.AnswerNote.Service;

import Project.OpenBook.Domain.AnswerNote.Domain.AnswerNote;
import Project.OpenBook.Domain.AnswerNote.Dto.AnswerNoteDto;
import Project.OpenBook.Domain.AnswerNote.Repository.AnswerNoteRepository;
import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.Customer.Repository.CustomerRepository;
import Project.OpenBook.Domain.ExamQuestion.Domain.ExamQuestion;
import Project.OpenBook.Domain.ExamQuestion.Repo.ExamQuestionRepository;
import Project.OpenBook.Handler.Exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static Project.OpenBook.Constants.ErrorCode.QUESTION_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class AnswerNoteService {

    private final AnswerNoteRepository answerNoteRepository;
    private final CustomerRepository customerRepository;
    private final ExamQuestionRepository examQuestionRepository;

    @Transactional
    public void addAnswerNote(Customer customer, AnswerNoteDto answerNoteDto) {
        Long questionId = answerNoteDto.getId();

        ExamQuestion examQuestion = examQuestionRepository.findById(questionId).orElseThrow(() -> {
            throw new CustomException(QUESTION_NOT_FOUND);
        });

        AnswerNote answerNote = new AnswerNote(customer, examQuestion);
        answerNoteRepository.save(answerNote);
    }

    @Transactional
    public void deleteAnswerNote(Customer customer, AnswerNoteDto answerNoteDto) {
        Long questionId = answerNoteDto.getId();

        ExamQuestion examQuestion = examQuestionRepository.findById(questionId).orElseThrow(() -> {
            throw new CustomException(QUESTION_NOT_FOUND);
        });

        answerNoteRepository.findByCustomerAndExamQuestion(customer, examQuestion)
                    .ifPresent(answerNoteRepository::delete);
    }

//    public List<Long> queryAnswerNotes(Customer customer) {
//        List<AnswerNote> answerNoteList = answerNoteRepository.queryAnswerNotes(customerId);
//        List<Long> questionIdList = answerNoteList.stream().map(a -> a.getQuestion().getId()).collect(Collectors.toList());
//        return questionIdList;
//    }
}
