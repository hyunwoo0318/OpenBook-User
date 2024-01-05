package Project.OpenBook.Domain.LearningRecord.KeywordLearningRecord.Repo;

import static Project.OpenBook.Domain.Keyword.Domain.QKeyword.keyword;
import static Project.OpenBook.Domain.LearningRecord.KeywordLearningRecord.Domain.QKeywordLearningRecord.keywordLearningRecord;
import static Project.OpenBook.Domain.Topic.Domain.QTopic.topic;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.LearningRecord.KeywordLearningRecord.Domain.KeywordLearningRecord;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class KeywordLearningRecordRepositoryCustomImpl implements
    KeywordLearningRecordRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<KeywordLearningRecord> queryKeywordLearningRecordsInKeywords(Customer customer,
        List<Long> keywordIdList) {
        return queryFactory.selectFrom(keywordLearningRecord)
            .leftJoin(keywordLearningRecord.keyword, keyword).fetchJoin()
            .where(keywordLearningRecord.customer.eq(customer))
            .where(keywordLearningRecord.keyword.id.in(keywordIdList))
            .fetch();
    }

    @Override
    public List<KeywordLearningRecord> queryKeywordLearningRecordsInQuestionCategory(
        Customer customer, Long questionCategoryId) {
        return queryFactory.selectFrom(keywordLearningRecord)
            .leftJoin(keywordLearningRecord.keyword, keyword).fetchJoin()
            .leftJoin(keyword.topic, topic).fetchJoin()
            .where(keywordLearningRecord.customer.eq(customer))
            .where(keyword.topic.questionCategory.id.eq(questionCategoryId))
            .fetch();
    }


}
