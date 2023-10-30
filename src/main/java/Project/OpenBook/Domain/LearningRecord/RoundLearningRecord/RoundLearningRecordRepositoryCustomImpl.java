package Project.OpenBook.Domain.LearningRecord.RoundLearningRecord;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static Project.OpenBook.Domain.LearningRecord.RoundLearningRecord.QRoundLearningRecord.roundLearningRecord;
import static Project.OpenBook.Domain.Round.Domain.QRound.round;

@RequiredArgsConstructor
public class RoundLearningRecordRepositoryCustomImpl implements RoundLearningRecordRepositoryCustom {
    private final JPAQueryFactory queryFactory;


    @Override
    public List<RoundLearningRecord> queryRoundLearningRecord(Customer customer) {
        return queryFactory.selectFrom(roundLearningRecord)
                .leftJoin(roundLearningRecord.round, round).fetchJoin()
                .where(roundLearningRecord.customer.eq(customer))
                .fetch();
    }
}
