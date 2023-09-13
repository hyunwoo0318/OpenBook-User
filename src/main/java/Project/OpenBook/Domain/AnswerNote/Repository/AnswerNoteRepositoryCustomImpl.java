package Project.OpenBook.Domain.AnswerNote.Repository;

import Project.OpenBook.Domain.AnswerNote.Domain.AnswerNote;

import Project.OpenBook.Domain.AnswerNote.Domain.QAnswerNote;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static Project.OpenBook.Domain.AnswerNote.Domain.QAnswerNote.answerNote;

@Repository
@RequiredArgsConstructor
public class AnswerNoteRepositoryCustomImpl implements AnswerNoteRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<AnswerNote> queryAnswerNote(Long customerId, Long questionId) {
        AnswerNote answerNote = queryFactory.selectFrom(QAnswerNote.answerNote)
                .where(QAnswerNote.answerNote.customer.id.eq(customerId))
                .where(QAnswerNote.answerNote.question.id.eq(questionId))
                .fetchOne();

        return Optional.ofNullable(answerNote);
    }

    @Override
    public List<AnswerNote> queryAnswerNotes(Long customerId) {
        return queryFactory.selectFrom(answerNote)
                .where(answerNote.customer.id.eq(customerId))
                .fetch();
    }
}
