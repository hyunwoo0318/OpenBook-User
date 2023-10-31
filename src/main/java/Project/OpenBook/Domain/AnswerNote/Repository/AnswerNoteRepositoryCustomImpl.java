package Project.OpenBook.Domain.AnswerNote.Repository;

import Project.OpenBook.Domain.AnswerNote.Domain.AnswerNote;
import Project.OpenBook.Domain.Customer.Domain.Customer;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static Project.OpenBook.Domain.AnswerNote.Domain.QAnswerNote.answerNote;
import static Project.OpenBook.Domain.ExamQuestion.Domain.QExamQuestion.examQuestion;


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
    @Override
    public List<AnswerNote> queryAnswerNotes(Integer roundNumber, Customer customer) {
        return queryFactory.selectFrom(answerNote)
                .leftJoin(answerNote.examQuestion, examQuestion).fetchJoin()
                .where(answerNote.customer.eq(customer))
                .where(examQuestion.round.number.eq(roundNumber))
                .fetch();
    }

}
