package Project.OpenBook.Domain.AnswerNote.Repository;

import Project.OpenBook.Domain.AnswerNote.Domain.AnswerNote;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;



@Repository
@RequiredArgsConstructor
public class AnswerNoteRepositoryCustomImpl implements AnswerNoteRepositoryCustom {

    private final JPAQueryFactory queryFactory;

//    @Override
//    public Optional<AnswerNote> queryAnswerNote(Long customerId, Long questionId) {
//        AnswerNote findAnswerNote = queryFactory.selectFrom(answerNote)
//                .where(answerNote.customer.id.eq(customerId))
//                .where(answerNote.question.id.eq(questionId))
//                .fetchOne();
//
//        return Optional.ofNullable(findAnswerNote);
//    }
//
//    @Override
//    public List<AnswerNote> queryAnswerNotes(Long customerId) {
//        return queryFactory.selectFrom(answerNote)
//                .where(answerNote.customer.id.eq(customerId))
//                .fetch();
//    }
}
