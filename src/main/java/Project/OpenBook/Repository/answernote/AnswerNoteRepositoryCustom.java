package Project.OpenBook.Repository.answernote;


import Project.OpenBook.Domain.AnswerNote;
import Project.OpenBook.Domain.Bookmark;

import java.util.List;

public interface AnswerNoteRepositoryCustom {

    public AnswerNote queryAnswerNote(Long customerId, Long questionId);

    public List<AnswerNote> queryAnswerNotes(Long customerId);
}
