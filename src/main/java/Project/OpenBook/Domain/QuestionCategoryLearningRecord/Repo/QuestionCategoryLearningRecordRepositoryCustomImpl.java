package Project.OpenBook.Domain.QuestionCategoryLearningRecord.Repo;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.QuestionCategoryLearningRecord.Domain.QuestionCategoryLearningRecord;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static Project.OpenBook.Domain.QuestionCategoryLearningRecord.Domain.QQuestionCategoryLearningRecord.questionCategoryLearningRecord;

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