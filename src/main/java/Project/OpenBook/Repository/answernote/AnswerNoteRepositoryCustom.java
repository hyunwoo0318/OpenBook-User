package Project.OpenBook.Repository.answernote;


import Project.OpenBook.Domain.AnswerNote;
import Project.OpenBook.Domain.Bookmark;

import java.util.List;
import java.util.Optional;

public interface AnswerNoteRepositoryCustom {

    public Optional<AnswerNote> queryAnswerNote(Long customerId, Long questionId);

    public List<AnswerNote> queryAnswerNotes(Long customerId);
}
