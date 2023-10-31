package Project.OpenBook.Domain.AnswerNote.Repository;

import Project.OpenBook.Domain.AnswerNote.Domain.AnswerNote;
import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.ExamQuestion.Domain.ExamQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface AnswerNoteRepository extends JpaRepository<AnswerNote, Long>, AnswerNoteRepositoryCustom {

    public Optional<AnswerNote> findByCustomerAndExamQuestion(Customer customer, ExamQuestion examQuestion);
}
