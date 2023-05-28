package Project.OpenBook.Repository.dupdate;

import Project.OpenBook.Domain.DupDate;
import Project.OpenBook.Domain.QDupDate;
import Project.OpenBook.Domain.QTopic;
import Project.OpenBook.Domain.Topic;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static Project.OpenBook.Domain.QDupDate.dupDate;
import static Project.OpenBook.Domain.QTopic.topic;

@Repository
@RequiredArgsConstructor
public class DupDateRepositoryCustomImpl implements DupDateRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    @Override
    public List<Topic> queryAnswerTopics(Integer startDate, Integer endDate) {
        return queryFactory.selectFrom(topic)
                .where(topic.startDate.gt(startDate))
                .where(topic.endDate.lt(endDate))
                .fetch();
    }

    @Override
    public List<Topic> queryDescriptionTopics(Integer startDate, Integer endDate) {
        return queryFactory.selectFrom(topic)
                .where(topic.startDate.lt(startDate))
                .where(topic.endDate.gt(endDate))
                .fetch();
    }

    @Override
    public List<DupDate> queryAllByTopic(String topicTitle) {
        return queryFactory.selectFrom(dupDate)
                .where(dupDate.answerTopic.title.eq(topicTitle).or(dupDate.descriptionTopic.title.eq(topicTitle)))
                .fetch();
    }

    @Override
    public Topic queryRandomAnswerTopic() {
        return queryFactory.selectDistinct(dupDate.answerTopic)
                .from(dupDate)
                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
                .limit(1)
                .fetchOne();
    }

    @Override
    public Topic queryRandomDescriptionTopic(Topic answerTopic) {
        return queryFactory.selectDistinct(dupDate.descriptionTopic)
                .from(dupDate)
                .where(dupDate.answerTopic.eq(answerTopic))
                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
                .limit(1)
                .fetchOne();
    }
}
