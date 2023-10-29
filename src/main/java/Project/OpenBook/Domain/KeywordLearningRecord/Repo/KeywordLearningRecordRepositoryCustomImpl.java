package Project.OpenBook.Domain.KeywordLearningRecord.Repo;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.KeywordLearningRecord.Domain.KeywordLearningRecord;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static Project.OpenBook.Domain.Keyword.Domain.QKeyword.keyword;
import static Project.OpenBook.Domain.KeywordLearningRecord.Domain.QKeywordLearningRecord.keywordLearningRecord;

@Repository
@RequiredArgsConstructor
public class KeywordLearningRecordRepositoryCustomImpl implements KeywordLearningRecordRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<KeywordLearningRecord> queryKeywordLearningRecordsInKeywords(Customer customer, List<Long> keywordIdList) {
        return queryFactory.selectFrom(keywordLearningRecord)
                .leftJoin(keywordLearningRecord.keyword, keyword).fetchJoin()
                .where(keywordLearningRecord.customer.eq(customer))
                .where(keywordLearningRecord.keyword.id.in(keywordIdList))
                .fetch();
    }

    @Override
    public List<KeywordLearningRecord> queryKeywordLearningRecordsInQuestionCategory(Customer customer, Long questionCategoryId) {
        return queryFactory.selectFrom(keywordLearningRecord)
                .leftJoin(keywordLearningRecord.keyword, keyword).fetchJoin()
                .where(keywordLearningRecord.customer.eq(customer))
                .where(keyword.topic.questionCategory.id.eq(questionCategoryId))
                .fetch();
    }
}
