package Project.OpenBook.Domain.QuestionCategoryLearningRecord;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static Project.OpenBook.Domain.QuestionCategoryLearningRecord.QQuestionCategoryLearningRecord.questionCategoryLearningRecord;

@Repository
@RequiredArgsConstructor
public class QuestionCategoryLearningRecordRepositoryCustomImpl implements QuestionCategoryLearningRecordRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<QuestionCategoryLearningRecord> queryQuestionRecords(Customer customer) {
        return queryFactory.selectFrom(questionCategoryLearningRecord)
                .leftJoin(questionCategoryLearningRecord.questionCategory).fetchJoin()
                .where(questionCategoryLearningRecord.customer.eq(customer))
                .fetch();
    }
}
