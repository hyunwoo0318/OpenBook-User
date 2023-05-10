package Project.OpenBook.Service;

import Project.OpenBook.Domain.*;
import Project.OpenBook.Dto.AnswerNoteDto;
import Project.OpenBook.Repository.CustomerRepository;
import Project.OpenBook.Repository.answernote.AnswerNoteRepository;
import Project.OpenBook.Repository.question.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnswerNoteService {

    private final AnswerNoteRepository answerNoteRepository;
    private final CustomerRepository customerRepository;
    private final QuestionRepository questionRepository;

    public AnswerNote addAnswerNote(AnswerNoteDto answerNoteDto) {
        Long customerId = answerNoteDto.getCustomerId();
        Long questionId = answerNoteDto.getQuestionId();

        Optional<Customer> customerOptional = customerRepository.findById(customerId);
        if (customerOptional.isEmpty()) {
            return null;
        }
        Customer customer = customerOptional.get();

        Optional<Question> questionOptional = questionRepository.findById(questionId);
        if(questionOptional.isEmpty()){
            return null;
        }
        Question question = questionOptional.get();

        AnswerNote answerNote = new AnswerNote(customer, question);
        answerNoteRepository.save(answerNote);
        return answerNote;
    }

    public boolean deleteAnswerNote(AnswerNoteDto answerNoteDto) {
        Long customerId = answerNoteDto.getCustomerId();
        Long questionId = answerNoteDto.getQuestionId();

        Optional<Customer> customerOptional = customerRepository.findById(customerId);
        if (customerOptional.isEmpty()) {
            return false;
        }


        Optional<Question> questionOptional = questionRepository.findById(questionId);
        if(questionOptional.isEmpty()){
            return false;
        }

        AnswerNote answerNote = answerNoteRepository.queryAnswerNote(customerId, questionId);
        answerNoteRepository.delete(answerNote);
        return true;
    }

    public List<Long> queryAnswerNotes(Long customerId) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);
        if (customerOptional.isEmpty()) {
            return null;
        }

        List<AnswerNote> answerNoteList = answerNoteRepository.queryAnswerNotes(customerId);
        List<Long> questionIdList = answerNoteList.stream().map(a -> a.getQuestion().getId()).collect(Collectors.toList());
        return questionIdList;
    }
}
