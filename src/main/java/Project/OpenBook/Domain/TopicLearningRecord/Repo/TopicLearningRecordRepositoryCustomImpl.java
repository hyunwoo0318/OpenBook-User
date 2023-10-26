package Project.OpenBook.Domain.TopicLearningRecord.Repo;

import Project.OpenBook.Domain.Topic.Domain.QTopic;
import Project.OpenBook.Domain.TopicLearningRecord.Domain.QTopicLearningRecord;
import Project.OpenBook.Domain.TopicLearningRecord.Domain.TopicLearningRecord;
import Project.OpenBook.Domain.Customer.Domain.Customer;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static Project.OpenBook.Domain.Topic.Domain.QTopic.topic;
import static Project.OpenBook.Domain.TopicLearningRecord.Domain.QTopicLearningRecord.topicLearningRecord;

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
}
