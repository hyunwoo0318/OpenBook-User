package Project.OpenBook.Repository.topicprogress;

import Project.OpenBook.Domain.QTopicProgress;
import Project.OpenBook.Domain.TopicProgress;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static Project.OpenBook.Domain.QTopicProgress.topicProgress;

@RequiredArgsConstructor
@Repository
public class TopicProgressRepositoryCustomImpl implements TopicProgressRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    @Override
    public TopicProgress queryTopicProgress(String topicTitle, Long customerId) {
        return queryFactory.selectFrom(topicProgress)
                .where(topicProgress.topic.title.eq(topicTitle))
                .where(topicProgress.customer.id.eq(customerId))
                .fetchOne();
    }
}
