package Project.OpenBook.Domain.AnswerNote.Repository;


import Project.OpenBook.Domain.AnswerNote.Domain.AnswerNote;
import Project.OpenBook.Domain.Customer.Domain.Customer;

import java.util.List;

public interface AnswerNoteRepositoryCustom {

//    public Optional<AnswerNote> queryAnswerNote(Long customerId, Long questionId);
//
    public List<AnswerNote> queryAnswerNotes(Integer roundNumber, Customer customer);
}
