package Project.OpenBook.Service;

import Project.OpenBook.Constants.ErrorCode;
import Project.OpenBook.CustomException;
import Project.OpenBook.Domain.*;
import Project.OpenBook.Dto.AnswerNoteDto;
import Project.OpenBook.Repository.customer.CustomerRepository;
import Project.OpenBook.Repository.answernote.AnswerNoteRepository;
import Project.OpenBook.Repository.question.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static Project.OpenBook.Constants.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class AnswerNoteService {

    private final AnswerNoteRepository answerNoteRepository;
    private final CustomerRepository customerRepository;
    private final QuestionRepository questionRepository;

    public AnswerNote addAnswerNote(AnswerNoteDto answerNoteDto) {
        Long customerId = answerNoteDto.getCustomerId();
        Long questionId = answerNoteDto.getQuestionId();

        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> {
            throw new CustomException(CUSTOMER_NOT_FOUND);
        });

        Question question = questionRepository.findById(questionId).orElseThrow(() -> {
            throw new CustomException(QUESTION_NOT_FOUND);
        });

        AnswerNote answerNote = new AnswerNote(customer, question);
        answerNoteRepository.save(answerNote);
        return answerNote;
    }

    public void deleteAnswerNote(AnswerNoteDto answerNoteDto) {
        Long customerId = answerNoteDto.getCustomerId();
        Long questionId = answerNoteDto.getQuestionId();

        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> {
            throw new CustomException(CUSTOMER_NOT_FOUND);
        });

        Question question = questionRepository.findById(questionId).orElseThrow(() -> {
            throw new CustomException(QUESTION_NOT_FOUND);
        });

        AnswerNote answerNote = answerNoteRepository.queryAnswerNote(customerId, questionId);
        answerNoteRepository.delete(answerNote);

    }

    public List<Long> queryAnswerNotes(Long customerId) {
         customerRepository.findById(customerId).orElseThrow(() -> {
             throw new CustomException(CUSTOMER_NOT_FOUND);
         });

        List<AnswerNote> answerNoteList = answerNoteRepository.queryAnswerNotes(customerId);
        List<Long> questionIdList = answerNoteList.stream().map(a -> a.getQuestion().getId()).collect(Collectors.toList());
        return questionIdList;
    }
}
