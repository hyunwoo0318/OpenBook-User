package Project.OpenBook.Domain.LearningRecord.QuestionCategoryLearningRecord.Repo;

import static Project.OpenBook.Domain.Customer.Domain.QCustomer.customer;
import static Project.OpenBook.Domain.LearningRecord.QuestionCategoryLearningRecord.Domain.QQuestionCategoryLearningRecord.questionCategoryLearningRecord;
import static Project.OpenBook.Domain.QuestionCategory.Domain.QQuestionCategory.questionCategory;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.LearningRecord.QuestionCategoryLearningRecord.Domain.QuestionCategoryLearningRecord;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QuestionCategoryLearningRecordRepositoryCustomImpl implements
    QuestionCategoryLearningRecordRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<QuestionCategoryLearningRecord> queryQuestionRecords(Customer customer) {
        return queryFactory.selectFrom(questionCategoryLearningRecord).distinct()
            .leftJoin(questionCategoryLearningRecord.questionCategory, questionCategory).fetchJoin()
            .leftJoin(questionCategory.topicList).fetchJoin()
            .where(questionCategoryLearningRecord.customer.eq(customer))
            .fetch();
    }

    @Override
    public List<QuestionCategoryLearningRecord> queryQuestionRecordsInKeywords(Customer customer,
        List<Long> questionCategoryIdList) {
        return queryFactory.selectFrom(questionCategoryLearningRecord)
            .leftJoin(questionCategoryLearningRecord.questionCategory, questionCategory).fetchJoin()
            .where(questionCategoryLearningRecord.customer.eq(customer))
            .where(questionCategoryLearningRecord.questionCategory.id.in(questionCategoryIdList))
            .fetch();
    }

    @Override
    public QuestionCategoryLearningRecord queryQuestionCategoryLowScore(Customer customer) {
        return queryFactory.selectFrom(questionCategoryLearningRecord).distinct()
            .leftJoin(questionCategoryLearningRecord.questionCategory, questionCategory).fetchJoin()
            .leftJoin(questionCategory.topicList).fetchJoin()
            .where(questionCategoryLearningRecord.customer.eq(customer))
            .orderBy(questionCategoryLearningRecord.score.asc())
            .limit(1)
            .fetchOne();
    }

    @Override
    public List<QuestionCategoryLearningRecord> queryQuestionRecordsForInit() {
        return queryFactory.selectFrom(questionCategoryLearningRecord)
            .leftJoin(questionCategoryLearningRecord.questionCategory, questionCategory).fetchJoin()
            .leftJoin(questionCategoryLearningRecord.customer, customer).fetchJoin()
            .fetch();
    }
}
