package Project.OpenBook.Repository.topic;

import Project.OpenBook.Domain.Topic;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.MathExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static Project.OpenBook.Domain.QTopic.topic;

@Repository
@RequiredArgsConstructor
public class TopicRepositoryCustomImpl implements TopicRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Topic queryRandTopicByCategory(String categoryName) {
         return queryFactory.selectFrom(topic)
                .where(topic.category.name.eq(categoryName))
                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
                .limit(1)
                .fetchOne();
    }
}
