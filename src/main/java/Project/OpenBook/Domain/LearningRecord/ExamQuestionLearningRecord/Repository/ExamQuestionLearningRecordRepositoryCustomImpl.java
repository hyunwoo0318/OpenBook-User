package Project.OpenBook.Domain.LearningRecord.ExamQuestionLearningRecord.Repository;

import static Project.OpenBook.Domain.ExamQuestion.Domain.QExamQuestion.examQuestion;
import static Project.OpenBook.Domain.LearningRecord.ExamQuestionLearningRecord.Domain.QExamQuestionLearningRecord.examQuestionLearningRecord;
import static Project.OpenBook.Domain.Round.Domain.QRound.round;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.LearningRecord.ExamQuestionLearningRecord.Domain.ExamQuestionLearningRecord;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ExamQuestionLearningRecordRepositoryCustomImpl
    implements ExamQuestionLearningRecordRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<ExamQuestionLearningRecord> queryExamQuestionLearningRecords(
      Integer roundNumber, Customer customer) {
    return queryFactory
        .selectFrom(examQuestionLearningRecord)
        .leftJoin(examQuestionLearningRecord.examQuestion, examQuestion)
        .fetchJoin()
        .leftJoin(examQuestion.round, round)
        .fetchJoin()
        .where(examQuestionLearningRecord.customer.eq(customer))
        .where(examQuestion.round.number.eq(roundNumber))
        .fetch();
  }

  @Override
  public List<ExamQuestionLearningRecord> queryExamQuestionLearningRecordsAnswerNoted(
      Customer customer) {
    return queryFactory
        .selectFrom(examQuestionLearningRecord)
        .leftJoin(examQuestionLearningRecord.examQuestion, examQuestion)
        .fetchJoin()
        .leftJoin(examQuestion.round, round)
        .fetchJoin()
        .where(examQuestionLearningRecord.customer.eq(customer))
        .where(examQuestionLearningRecord.answerNoted.isTrue())
        .fetch();
  }

  @Override
  public List<ExamQuestionLearningRecord> queryExamQuestionLearningRecords(
      Customer customer, List<Long> examQuestionIdList) {
    return queryFactory
        .selectFrom(examQuestionLearningRecord)
        .leftJoin(examQuestionLearningRecord.examQuestion, examQuestion)
        .fetchJoin()
        .leftJoin(examQuestion.round, round)
        .fetchJoin()
        .where(examQuestionLearningRecord.customer.eq(customer))
        .where(examQuestionLearningRecord.examQuestion.id.in(examQuestionIdList))
        .fetch();
  }

  @Override
  public void deleteAllInBatchByCustomer(Customer customer) {
    queryFactory
        .delete(examQuestionLearningRecord)
        .where(examQuestionLearningRecord.customer.eq(customer))
        .execute();
  }
}
