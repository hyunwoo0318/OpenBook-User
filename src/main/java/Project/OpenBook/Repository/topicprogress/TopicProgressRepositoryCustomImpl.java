package Project.OpenBook.Repository.topicprogress;

import Project.OpenBook.Domain.QTopicProgress;
import Project.OpenBook.Domain.TopicProgress;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static Project.OpenBook.Domain.QTopicProgress.topicProgress;

@RequiredArgsConstructor
@Repository
public class TopicProgressRepositoryCustomImpl implements TopicProgressRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    @Override
    public Optional<TopicProgress> queryTopicProgress(Long customerId, String topicTitle) {
        TopicProgress findTopicProgress = queryFactory.selectFrom(topicProgress)
                .where(topicProgress.topic.title.eq(topicTitle))
                .where(topicProgress.customer.id.eq(customerId))
                .fetchOne();
        return Optional.ofNullable(findTopicProgress);
    }

    @Override
    public List<TopicProgress> queryTopicProgresses(Long customerId,Integer chapterNum) {
        return queryFactory.selectFrom(topicProgress)
                .where(topicProgress.customer.id.eq(customerId))
                .where(topicProgress.topic.chapter.number.eq(chapterNum))
                .fetch();
    }
}
