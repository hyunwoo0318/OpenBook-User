package Project.OpenBook.Domain.LearningRecord.TopicLearningRecord.Repo;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.LearningRecord.TopicLearningRecord.Domain.TopicLearningRecord;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static Project.OpenBook.Domain.LearningRecord.TopicLearningRecord.Domain.QTopicLearningRecord.topicLearningRecord;
import static Project.OpenBook.Domain.Topic.Domain.QTopic.topic;


@Repository
@RequiredArgsConstructor
public class TopicLearningRecordRepositoryCustomImpl implements TopicLearningRecordRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<TopicLearningRecord> queryTopicLearningRecordsInKeywords(Customer customer, List<Long> topicIdList) {
        return queryFactory.selectFrom(topicLearningRecord)
                .leftJoin(topicLearningRecord.topic, topic).fetchJoin()
                .where(topicLearningRecord.customer.eq(customer))
                .where(topicLearningRecord.topic.id.in(topicIdList))
                .fetch();
    }

    @Override
    public List<TopicLearningRecord> queryTopicLearningRecordsInQuestionCategory(Customer customer, Long questionCategoryId) {
        return queryFactory.selectFrom(topicLearningRecord).distinct()
                .leftJoin(topicLearningRecord.topic, topic).fetchJoin()
                .leftJoin(topic.keywordList).fetchJoin()
                .where(topicLearningRecord.customer.eq(customer))
                .where(topicLearningRecord.topic.questionCategory.id.eq(questionCategoryId))
                .fetch();
    }
}
