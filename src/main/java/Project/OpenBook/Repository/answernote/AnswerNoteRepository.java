package Project.OpenBook.Repository.answernote;

import Project.OpenBook.Domain.AnswerNote;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AnswerNoteRepository extends JpaRepository<AnswerNote, Long>, AnswerNoteRepositoryCustom{
}
