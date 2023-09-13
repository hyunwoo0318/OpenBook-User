package Project.OpenBook.Domain.AnswerNote.Repository;

import Project.OpenBook.Domain.AnswerNote.Domain.AnswerNote;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AnswerNoteRepository extends JpaRepository<AnswerNote, Long>, AnswerNoteRepositoryCustom{
}
