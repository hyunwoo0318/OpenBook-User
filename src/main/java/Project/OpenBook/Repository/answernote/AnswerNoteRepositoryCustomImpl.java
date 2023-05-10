package Project.OpenBook.Repository.answernote;

import Project.OpenBook.Domain.AnswerNote;
import Project.OpenBook.Domain.Bookmark;
import Project.OpenBook.Domain.QAnswerNote;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static Project.OpenBook.Domain.QAnswerNote.answerNote;
import static Project.OpenBook.Domain.QBookmark.bookmark;

@Repository
@RequiredArgsConstructor
public class AnswerNoteRepositoryCustomImpl implements AnswerNoteRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public AnswerNote queryAnswerNote(Long customerId, Long questionId) {
        return queryFactory.selectFrom(answerNote)
                .where(answerNote.customer.id.eq(customerId))
                .where(answerNote.question.id.eq(questionId))
                .fetchOne();
    }

    @Override
    public List<AnswerNote> queryAnswerNotes(Long customerId) {
        return queryFactory.selectFrom(answerNote)
                .where(answerNote.customer.id.eq(customerId))
                .fetch();
    }
}
