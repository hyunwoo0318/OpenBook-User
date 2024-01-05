package Project.OpenBook.Domain.LearningRecord.RoundLearningRecord;

import static Project.OpenBook.Domain.LearningRecord.RoundLearningRecord.QRoundLearningRecord.roundLearningRecord;
import static Project.OpenBook.Domain.Round.Domain.QRound.round;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RoundLearningRecordRepositoryCustomImpl implements
    RoundLearningRecordRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    @Override
    public List<RoundLearningRecord> queryRoundLearningRecord(Customer customer) {
        return queryFactory.selectFrom(roundLearningRecord)
            .leftJoin(roundLearningRecord.round, round).fetchJoin()
            .where(roundLearningRecord.customer.eq(customer))
            .fetch();
    }

    @Override
    public Optional<RoundLearningRecord> queryRoundLearningRecord(Customer customer,
        Integer roundNumber) {
        RoundLearningRecord record = queryFactory.selectFrom(roundLearningRecord).distinct()
            .leftJoin(roundLearningRecord.round, round).fetchJoin()
            .leftJoin(round.examQuestionList).fetchJoin()
            .where(roundLearningRecord.customer.eq(customer))
            .where(roundLearningRecord.round.number.eq(roundNumber))
            .fetchOne();
        return Optional.ofNullable(record);
    }
}
