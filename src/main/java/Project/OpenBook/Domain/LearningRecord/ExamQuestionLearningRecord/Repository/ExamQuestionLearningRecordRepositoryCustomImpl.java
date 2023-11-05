package Project.OpenBook.Domain.LearningRecord.ExamQuestionLearningRecord.Repository;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.LearningRecord.ExamQuestionLearningRecord.Domain.ExamQuestionLearningRecord;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static Project.OpenBook.Domain.ExamQuestion.Domain.QExamQuestion.examQuestion;
import static Project.OpenBook.Domain.LearningRecord.ExamQuestionLearningRecord.Domain.QExamQuestionLearningRecord.examQuestionLearningRecord;


@Repository
@RequiredArgsConstructor
public class ExamQuestionLearningRecordRepositoryCustomImpl implements ExamQuestionLearningRecordRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    @Override
    public List<ExamQuestionLearningRecord> queryExamQuestionLearningRecords(Integer roundNumber, Customer customer) {
        return queryFactory.selectFrom(examQuestionLearningRecord)
                .leftJoin(examQuestionLearningRecord.examQuestion, examQuestion).fetchJoin()
                .where(examQuestionLearningRecord.customer.eq(customer))
                .where(examQuestion.round.number.eq(roundNumber))
                .fetch();
    }


    @Override
    public List<ExamQuestionLearningRecord> queryExamQuestionLearningRecords(Customer customer, List<Long> examQuestionIdList) {
        return queryFactory.selectFrom(examQuestionLearningRecord)
                .leftJoin(examQuestionLearningRecord.examQuestion, examQuestion).fetchJoin()
                .where(examQuestionLearningRecord.customer.eq(customer))
                .where(examQuestionLearningRecord.examQuestion.id.in(examQuestionIdList))
                .fetch();
    }
}
